# MySQL接続情報の確認手順

## 1. MySQLがインストールされているか確認

```powershell
mysql --version
```

インストールされていない場合は、MySQL公式サイトからダウンロードしてインストールしてください。

## 2. MySQLに接続してみる

### 方法A: rootユーザーで接続（パスワードありの場合）

```powershell
mysql -u root -p
```

パスワードの入力を求められるので、インストール時に設定したパスワードを入力してください。

### 方法B: rootユーザーで接続（パスワードなしの場合）

```powershell
mysql -u root
```

### 方法C: パスワードがわからない場合

1. **XAMPPやMAMPを使用している場合**:
   - XAMPP: デフォルトでパスワードは空（なし）
   - MAMP: デフォルトでユーザー名は`root`、パスワードは`root`

2. **MySQLを個別にインストールした場合**:
   - インストール時に設定したパスワードを使用
   - パスワードを忘れた場合は、MySQLのパスワードリセット手順を参照

## 3. 接続に成功したら、現在のユーザーを確認

MySQLコマンドラインで以下のコマンドを実行：

```sql
SELECT USER();
```

または

```sql
SELECT CURRENT_USER();
```

これで現在接続しているユーザー名が表示されます。

## 4. application.ymlの設定

確認したユーザー名とパスワードを`src/main/resources/application.yml`に設定します：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/clinic_booking_db?useSSL=false&serverTimezone=Asia/Tokyo&characterEncoding=UTF-8
    username: root  # ここに確認したユーザー名を入力
    password: root  # ここに確認したパスワードを入力（パスワードがない場合は空文字 ""）
```

## よくある設定例

### XAMPPを使用している場合

```yaml
spring:
  datasource:
    username: root
    password: ""  # パスワードなし（空文字）
```

### MAMPを使用している場合

```yaml
spring:
  datasource:
    username: root
    password: root
```

### MySQLを個別にインストールした場合（標準設定）

```yaml
spring:
  datasource:
    username: root
    password: インストール時に設定したパスワード
```

## 接続できない場合のトラブルシューティング

### エラー: "Access denied for user 'root'@'localhost'"

→ パスワードが間違っているか、そのユーザーが存在しません。

### エラー: "Can't connect to MySQL server"

→ MySQLサービスが起動していない可能性があります。

**Windowsの場合**:
```powershell
# サービスを確認
Get-Service -Name MySQL*

# サービスを起動（サービス名は環境によって異なります）
Start-Service MySQL80
# または
net start MySQL80
```

**XAMPPの場合**:
- XAMPPコントロールパネルでMySQLを起動してください

**MAMPの場合**:
- MAMPのコントロールパネルでMySQLサーバーを起動してください

## 新しいユーザーを作成する（推奨）

セキュリティのため、アプリケーション専用のユーザーを作成することを推奨します：

```sql
-- MySQLにrootで接続後
CREATE DATABASE IF NOT EXISTS clinic_booking_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE USER 'clinic_user'@'localhost' IDENTIFIED BY 'secure_password';
GRANT ALL PRIVILEGES ON clinic_booking_db.* TO 'clinic_user'@'localhost';
FLUSH PRIVILEGES;
```

この場合のapplication.yml:

```yaml
spring:
  datasource:
    username: clinic_user
    password: secure_password
```
