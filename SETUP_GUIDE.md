# セットアップガイド

## 前提条件の確認

以下のコマンドで環境を確認してください：

```powershell
# Java 17以上がインストールされているか確認
java -version

# Maven 3.6以上がインストールされているか確認
mvn -version

# MySQLがインストールされているか確認（オプション）
mysql --version
```

## セットアップ手順

### 1. プロジェクトディレクトリに移動

プロジェクトのルートディレクトリ（pom.xmlがあるディレクトリ）に移動してください。

```powershell
cd "C:\Users\sunya\OneDrive - 大阪工業大学\ゼミ\診療予約管理システム\ClinicApp_Method3_EssentialSpec"
```

### 2. Mavenの依存関係をダウンロード

以下のコマンドを実行して、プロジェクトの依存関係をダウンロードします：

```powershell
mvn clean install -DskipTests
```

または、依存関係のみをダウンロードする場合：

```powershell
mvn dependency:resolve
```

### 3. MySQLデータベースのセットアップ

#### 3-1. MySQLに接続

```powershell
mysql -u root -p
```

パスワードを入力します。

#### 3-2. データベースとテーブルを作成

MySQLコマンドラインで以下のSQLを実行するか、またはファイルから実行します：

```sql
SOURCE src/main/resources/schema.sql;
```

または、コマンドラインから直接実行する場合：

```powershell
mysql -u root -p < src/main/resources/schema.sql
```

#### 3-3. データベース接続情報の確認

##### MySQLユーザー名・パスワードの確認方法

MySQLのユーザー名とパスワードを確認するには、以下の方法があります：

**方法1: MySQLコマンドラインで確認**

```powershell
# MySQLに接続して現在のユーザーを確認
mysql -u root -p
```

接続後、以下のSQLコマンドで現在のユーザーを確認できます：

```sql
SELECT USER();
-- または
SELECT CURRENT_USER();
```

**方法2: デフォルト設定を使用する場合**

多くの環境では、デフォルトで以下の設定が使われています：
- **ユーザー名**: `root`
- **パスワード**: インストール時に設定したパスワード（設定していない場合は空）

**方法3: 新しいユーザーを作成する場合**

セキュリティ上、専用のユーザーを作成することを推奨します：

```sql
-- MySQLにrootで接続後
CREATE USER 'clinic_user'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON clinic_booking_db.* TO 'clinic_user'@'localhost';
FLUSH PRIVILEGES;
```

この場合の接続情報：
- **ユーザー名**: `clinic_user`
- **パスワード**: `your_password`（設定したパスワード）

##### application.ymlの設定変更

`src/main/resources/application.yml` を開いて、データベース接続情報を確認・変更してください：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/clinic_booking_db?useSSL=false&serverTimezone=Asia/Tokyo&characterEncoding=UTF-8
    username: root  # あなたのMySQLユーザー名に変更（多くの場合はroot）
    password: root  # あなたのMySQLパスワードに変更（インストール時に設定したパスワード）
```

**注意**: 
- パスワードに特殊文字が含まれている場合は、YAMLの引用符で囲む必要がある場合があります
- 例: `password: "my@password123"`

### 4. アプリケーションの起動

以下のコマンドでアプリケーションを起動します：

```powershell
mvn spring-boot:run
```

アプリケーションが起動したら、ブラウザで以下のURLにアクセスしてください：

```
http://localhost:8080
```

### 5. デフォルト管理者アカウントでログイン

- **管理者ID**: `admin`
- **パスワード**: `admin`

## トラブルシューティング

### Mavenのビルドエラーが発生する場合

1. Javaのバージョンを確認してください（Java 17以上が必要です）
2. Mavenのバージョンを確認してください（Maven 3.6以上が必要です）
3. インターネット接続を確認してください（依存関係のダウンロードに必要です）

### データベース接続エラーが発生する場合

1. MySQLが起動しているか確認してください
2. `application.yml`のデータベース接続情報が正しいか確認してください
3. データベース`clinic_booking_db`が作成されているか確認してください

```sql
SHOW DATABASES;
USE clinic_booking_db;
SHOW TABLES;
```

### ポート8080が使用中の場合

`application.yml`でポート番号を変更できます：

```yaml
server:
  port: 8081  # 他のポート番号に変更
```

## テストの実行

単体テストを実行する場合：

```powershell
mvn test
```

特定のテストクラスのみを実行する場合：

```powershell
mvn test -Dtest=BookingServiceTest
```
