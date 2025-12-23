# IDEの完全終了方法

## Eclipseの場合

### 方法1: 通常の終了
1. Eclipseのウィンドウを閉じる（右上の×ボタン）
2. 全てのEclipseウィンドウを閉じる
3. 数秒待って、プロセスが完全に終了するのを確認

### 方法2: プロセスが残っている場合の確認と終了

**PowerShellで確認：**

```powershell
# Eclipseのプロセスを確認
Get-Process | Where-Object {$_.ProcessName -like "*eclipse*" -or $_.ProcessName -like "*javaw*"} | Select-Object ProcessName, Id, StartTime
```

**プロセスが見つかった場合、終了：**

```powershell
# プロセスIDを指定して終了（<ID>を実際のIDに置き換える）
Stop-Process -Id <ID> -Force

# または、プロセス名で終了
Stop-Process -Name eclipse -Force -ErrorAction SilentlyContinue
Stop-Process -Name javaw -Force -ErrorAction SilentlyContinue
```

## IntelliJ IDEAの場合

### 方法1: 通常の終了
1. IntelliJ IDEAのウィンドウを閉じる
2. タスクトレイ（画面右下）にアイコンが残っていないか確認
3. 残っている場合は右クリックして「Exit」を選択

### 方法2: プロセスが残っている場合の確認と終了

**PowerShellで確認：**

```powershell
# IntelliJ IDEAのプロセスを確認
Get-Process | Where-Object {$_.ProcessName -like "*idea*" -or $_.ProcessName -like "*idea64*"} | Select-Object ProcessName, Id, StartTime
```

**プロセスが見つかった場合、終了：**

```powershell
# プロセス名で終了
Stop-Process -Name idea64 -Force -ErrorAction SilentlyContinue
Stop-Process -Name idea -Force -ErrorAction SilentlyContinue
```

## 全てのJavaプロセスを確認・終了する方法

**全てのJavaプロセスを確認：**

```powershell
Get-Process | Where-Object {$_.ProcessName -like "*java*"} | Select-Object ProcessName, Id, StartTime, Path
```

**注意**: 他のJavaアプリケーションも終了してしまう可能性があるため、慎重に実行してください。

**特定のプロセスだけを終了（例：eclipseの場合）：**

```powershell
# プロセスを確認
$processes = Get-Process | Where-Object {$_.Path -like "*eclipse*" -or $_.Path -like "*idea*"}

# 確認してから終了
$processes | Select-Object ProcessName, Id, Path
$processes | Stop-Process -Force
```

## タスクマネージャーを使用する方法（GUI）

1. **Ctrl + Shift + Esc** を押してタスクマネージャーを開く
2. 「詳細」タブを選択
3. 以下のプロセス名を探して、右クリック → 「タスクの終了」
   - Eclipse: `eclipse.exe`, `javaw.exe`（Eclipseに関連するもの）
   - IntelliJ IDEA: `idea64.exe`, `idea.exe`
   - 一般的なJava: `java.exe`, `javaw.exe`

## 確認方法：プロセスが完全に終了したか確認

IDEを閉じた後、以下のコマンドで確認：

```powershell
# Eclipseのプロセスが残っていないか確認
Get-Process | Where-Object {$_.ProcessName -like "*eclipse*"}

# IntelliJ IDEAのプロセスが残っていないか確認
Get-Process | Where-Object {$_.ProcessName -like "*idea*"}

# 何も表示されなければ、完全に終了しています
```

## Mavenビルドを実行

IDEが完全に終了したことを確認したら：

```powershell
# プロジェクトディレクトリに移動（既にいる場合は不要）
cd "C:\Users\sunya\OneDrive - 大阪工業大学\ゼミ\診療予約管理システム\ClinicApp_Method3_EssentialSpec"

# クリーンビルドを実行
mvn clean install -DskipTests
```

## トラブルシューティング

### 「プロセスが見つかりません」エラーが出る場合

プロセスは既に終了している可能性があります。そのままMavenコマンドを実行してください。

### それでもtargetディレクトリが削除できない場合

手動でtargetディレクトリを削除してください：

```powershell
# プロジェクトディレクトリに移動
cd "C:\Users\sunya\OneDrive - 大阪工業大学\ゼミ\診療予約管理システム\ClinicApp_Method3_EssentialSpec"

# targetディレクトリを削除（エラーを無視して続行）
Remove-Item -Path target -Recurse -Force -ErrorAction SilentlyContinue

# その後、ビルドを実行（cleanは不要）
mvn install -DskipTests
```
