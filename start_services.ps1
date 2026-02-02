# Start all microservices
$basePath = "c:\Users\Admin\Downloads\MicroServices"
$backendPath = "$basePath\IndiChessBackend"
$jarPath = "$backendPath\target\IndiChessBackend-0.0.1-SNAPSHOT.jar"

# Start ChessBot
Write-Host "Starting ChessBot on port 8086..."
Start-Process -FilePath "java" -ArgumentList "ChessBot" -WorkingDirectory $basePath -WindowStyle Minimized

# Wait for ChessBot to start
Start-Sleep -Seconds 3

# Start Eureka Server
Write-Host "Starting EurekaServer on port 8761..."
Start-Process -FilePath "java" -ArgumentList "-jar", "$jarPath", "--server.port=8761", "--spring.application.name=EurekaServer" -WorkingDirectory $backendPath -WindowStyle Minimized

# Start remaining services
$services = @(
    @{ port = 8082; name = "UserService" },
    @{ port = 8083; name = "GameService" },
    @{ port = 8084; name = "MatchmakingService" },
    @{ port = 8085; name = "NotificationService" },
    @{ port = 8081; name = "AuthService" },
    @{ port = 8080; name = "APIGateway" }
)

foreach ($service in $services) {
    Write-Host "Starting $($service.name) on port $($service.port)..."
    Start-Process -FilePath "java" -ArgumentList "-jar", "$jarPath", "--server.port=$($service.port)", "--spring.application.name=$($service.name)" -WorkingDirectory $backendPath -WindowStyle Minimized
    Start-Sleep -Seconds 2
}

Write-Host "All services started!"
