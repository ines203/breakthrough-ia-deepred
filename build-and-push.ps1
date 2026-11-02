# =====================================================================
# Build + publication des images DeepRed sur Docker Hub 
# Usage : .\build-and-push.ps1            (tag latest)
#         .\build-and-push.ps1 -Tag v2    (autre tag)
# Prerequis : Docker Desktop ouvert, docker login fait, Maven dans le PATH
# A lancer dans une NOUVELLE console (pas apres minikube docker-env)
# =====================================================================

param([string]$Tag = "latest")

$DockerUser = "cyrjunior"

# Dossier de chaque service (relatif a la racine du repo)
# A AJUSTER quand Justin et Ines auront cree leurs projets
$Services = @(
    @{ Name = "deepred-engine";  Path = "deepRed/engine"  },
    @{ Name = "deepred-gateway"; Path = "deepRed/gateway" }
)

$Root = $PSScriptRoot

foreach ($s in $Services) {
    $dir   = Join-Path $Root $s.Path
    $image = "$DockerUser/$($s.Name):$Tag"

    Write-Host "`n=== $($s.Name) ===" -ForegroundColor Cyan

    if (-not (Test-Path (Join-Path $dir "pom.xml"))) {
        Write-Host "pom.xml introuvable dans $dir - service ignore." -ForegroundColor Yellow
        continue
    }

    Push-Location $dir
    try {
        Write-Host "Compilation Maven..."
        mvn clean package -DskipTests -q
        if ($LASTEXITCODE -ne 0) { throw "Echec de la compilation Maven" }

        Write-Host "Construction de l'image $image..."
        docker build -t $image .
        if ($LASTEXITCODE -ne 0) { throw "Echec du docker build" }

        Write-Host "Publication sur Docker Hub..."
        docker push $image
        if ($LASTEXITCODE -ne 0) { throw "Echec du docker push (docker login fait ?)" }

        Write-Host "$image publiee." -ForegroundColor Green
    }
    catch {
        Write-Host "ERREUR pour $($s.Name) : $_" -ForegroundColor Red
        Pop-Location
        exit 1
    }
    Pop-Location
}

Write-Host "`nTermine. Verifie les tags sur https://hub.docker.com/u/$DockerUser" -ForegroundColor Green