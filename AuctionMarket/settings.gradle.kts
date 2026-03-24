rootProject.name = "AuctionMarket"

// 공통 모듈
include("auction-common")
include("auction-domain")
include("auction-infra")

// 애플리케이션(API) 모듈
include("auction-app-user")
include("auction-app-seller")
include("auction-app-admin")