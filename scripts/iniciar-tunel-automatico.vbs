' Lanza el vigilante del tunel adb sin mostrar ninguna ventana.
Set fso = CreateObject("Scripting.FileSystemObject")
carpeta = fso.GetParentFolderName(WScript.ScriptFullName)
Set shell = CreateObject("WScript.Shell")
shell.Run "powershell.exe -NoProfile -WindowStyle Hidden -ExecutionPolicy Bypass -File """ & carpeta & "\mantener-tunel.ps1""", 0, False
