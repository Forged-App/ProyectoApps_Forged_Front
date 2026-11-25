param(
    [int]$IntervaloSegundos = 3
)

# Mantiene el tunel adb reverse tcp:8080 -> tcp:8080 activo para todos los
# dispositivos/emuladores conectados. Se ejecuta en segundo plano al iniciar
# Windows, asi la app puede abrirse directamente sin ejecutar nada a mano.

$adb = Join-Path $env:LOCALAPPDATA "Android\Sdk\platform-tools\adb.exe"
if (-not (Test-Path $adb)) {
    # Si adb esta en el PATH, usarlo.
    $adb = "adb"
}

while ($true) {
    try {
        $lineas = & $adb devices 2>$null
        $seriales = $lineas |
            Select-String -Pattern "^\S+\s+device$" |
            ForEach-Object { ($_.Line -split "\s+")[0] }

        foreach ($serial in $seriales) {
            $lista = & $adb -s $serial reverse --list 2>$null
            if (-not ($lista -match "tcp:8080")) {
                & $adb -s $serial reverse tcp:8080 tcp:8080 2>$null | Out-Null
            }
        }
    } catch {
        # Si adb no esta disponible o el emulador esta arrancando, reintenta.
    }
    Start-Sleep -Seconds $IntervaloSegundos
}
