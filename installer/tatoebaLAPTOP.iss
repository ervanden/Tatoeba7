; Script generated by the Inno Setup Script Wizard.
; SEE THE DOCUMENTATION FOR DETAILS ON CREATING INNO SETUP SCRIPT FILES!

#define MyAppName "tatoeba7"
#define MyAppVersion "2.0"
#define MyAppPublisher "ervanden"
#define MyAppExeName "Tatoeba7.jar"

; settings on asus
#define TatoebaLocation "C:\Users\erikv\Documents"
#define MyProjectsLocation "C:\Users\erikv\Documents\NetBeansProjects"
#define MyOutputDir "C:\Users\erikv\Documents"

; settings on dell
;#define TatoebaLocation "C:\Users\ervanden\Documents"
;#define MyProjectsLocation "C:\Users\ervanden\Documents\NetBeansProjects"
;#define MyOutputDir "C:\Users\ervanden\Documents"

[Setup]
; NOTE: The value of AppId uniquely identifies this application.
; Do not use the same AppId value in installers for other applications.
; (To generate a new GUID, click Tools | Generate GUID inside the IDE.)
AppId={{814DD20F-DFBD-4F70-9F1A-A14F6D5D3A6F}}
AppName={#MyAppName}
AppVersion={#MyAppVersion}
;AppVerName={#MyAppName} {#MyAppVersion}
AppPublisher={#MyAppPublisher}
DefaultDirName={pf}\{#MyAppName}
DefaultGroupName={#MyAppName}
DisableProgramGroupPage=yes
OutputDir={#MyOutputDir}
OutputBaseFilename=TatoebaInstaller
Compression=lzma
SolidCompression=yes

[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"

[Tasks]
Name: "desktopicon"; Description: "{cm:CreateDesktopIcon}"; GroupDescription: "{cm:AdditionalIcons}"; Flags: unchecked
Name: "quicklaunchicon"; Description: "{cm:CreateQuickLaunchIcon}"; GroupDescription: "{cm:AdditionalIcons}"; Flags: unchecked; OnlyBelowVersion: 0,6.1

[Files]
Source: "{#MyProjectsLocation}\Tatoeba7\dist\Tatoeba7.jar"; DestDir: "{app}"; Flags: ignoreversion
Source: "{#TatoebaLocation}\Tatoeba\*"; DestDir: "{userdocs}\Tatoeba"; Flags: ignoreversion recursesubdirs
Source: "{#TatoebaLocation}\Tatoeba\Images\tatoeba.ico"; DestDir: "{app}"; Flags: ignoreversion
; NOTE: Don't use "Flags: ignoreversion" on any shared system files

[Icons]
Name: "{group}\{#MyAppName}"; Filename: "{app}\{#MyAppExeName}" ;IconFilename: "{app}\tatoeba.ico"
Name: "{commondesktop}\{#MyAppName}"; Filename: "{app}\{#MyAppExeName}"; Tasks: desktopicon; IconFilename: "{app}\tatoeba.ico"
Name: "{userappdata}\Microsoft\Internet Explorer\Quick Launch\{#MyAppName}"; Filename: "{app}\{#MyAppExeName}"; Tasks: quicklaunchicon;IconFilename: "{app}\tatoeba.ico"

[Run]
Filename: "{app}\{#MyAppExeName}"; Description: "{cm:LaunchProgram,{#StringChange(MyAppName, '&', '&&')}}"; Flags: shellexec postinstall skipifsilent

