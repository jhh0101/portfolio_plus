package org.example.auctioncommon.domain.user.service

import org.example.auctioncommon.domain.auth.dto.SecurityUser
import org.example.auctioncommon.domain.bid.repository.BidRepository
import org.example.auctioncommon.domain.product.repository.ProductRepository
import org.example.auctioncommon.domain.user.dto.*
import org.example.auctioncommon.domain.user.entity.Role
import org.example.auctioncommon.domain.user.entity.User
import org.example.auctioncommon.domain.user.entity.UserStatus
import org.example.auctioncommon.domain.user.error.UserErrorCode
import org.example.auctioncommon.domain.user.repository.UserRepository
import org.example.auctioncommon.global.error.CustomException
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class UserService(
    private val bidRepository: BidRepository,
    private val productRepository: ProductRepository,
//    private val refreshTokenService: RefreshTokenService,
    private val passwordEncoder: PasswordEncoder,
    private val userProcessor: UserProcessor,
    private val userRepository: UserRepository
) : UserDetailsService {
    private val log = LoggerFactory.getLogger(javaClass)

    @Transactional
    fun signup(request: UserSingupRequest): UserResponse {
        val existingUser: User? = userRepository.findByEmail(request.email)
        val isDuplicateNickname: Boolean = userRepository.existsByNickname(request.nickname)

        userProcessor.validateSignup(existingUser, isDuplicateNickname)

        val newUser = User(
            email = request.email,
            username = request.username,
            nickname = request.nickname,
            baseAddress = request.baseAddress,
            detailAddress = request.detailAddress,
            phone = request.phone,
            password = passwordEncoder.encode(request.password),
            point = 0L,
            avgRating = 0.0,
            status = UserStatus.NORMAL,
            role = Role.USER
        )

        val saveUser = userRepository.save(newUser)
        log.info(
            "User created: userId={}, email={}, nickname={}",
            saveUser.userId,
            saveUser.email,
            saveUser.nickname
        )

        return saveUser.toDto()
    }

    @Transactional
    fun withdrawn(userId: Long, request: UserWithdrawnRequest): UserDeleteResponse {
        val user: User = userRepository.findByIdOrNull(userId)
            ?: throw CustomException(UserErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다.")

        val matchesPassword: Boolean = passwordEncoder.matches(request.password, user.password)

        val bidCount = bidRepository.bidCount(userId)
        val productCount = productRepository.productCount(userId)

        userProcessor.validateWithdrawn(matchesPassword, user, bidCount, productCount)

        return user.toDeleteDto()
    }

    fun profile(userId: Long): UserProfileResponse {
        val user = userRepository.findByIdOrNull(userId)
            ?: throw CustomException(UserErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다.")
        return user.toProfileDto()
    }

    @Transactional
    fun updateUser(userId: Long, request: UserUpdateRequest): UserProfileResponse {
        val user: User = userRepository.findByIdOrNull(userId)
            ?: throw CustomException(UserErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다.")

        val isDuplicateNickname: Boolean = userRepository.existsByNickname(request.nickname)

        val command = request.toUpdateCommand()

        userProcessor.validateUpdateUser(user, isDuplicateNickname, command)

        return user.toProfileDto()
    }

    @Transactional
    fun updatePassword(userId: Long, request: UserNewPasswordRequest) {

        val user = userRepository.findByIdOrNull(userId)
            ?: throw CustomException(UserErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다.")

        val matchesPassword: Boolean = passwordEncoder.matches(request.currentPassword, user.password)
        val encodedPassword: String = passwordEncoder.encode(request.newPassword).toString()

        userProcessor.validateUpdatePassword(user, request.newPassword, request.confirmPassword, matchesPassword, encodedPassword)
    }

    fun withdrawalStatus(userId: Long): WithdrawalStatusResponse {
        val user = userRepository.findByIdOrNull(userId)
            ?: throw CustomException(UserErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다.")

        val bidCount: Long = bidRepository.bidCount(userId)
        val productCount: Long = productRepository.productCount(userId)

        return user.toWithdrawalStatusDto(bidCount, productCount)
    }


    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(email: String): UserDetails {
        val user: User = userRepository.findByEmail(email)
            ?: throw CustomException(UserErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다.")

        userProcessor.loadUserByUsername(user)

        return SecurityUser(user)
    }
}