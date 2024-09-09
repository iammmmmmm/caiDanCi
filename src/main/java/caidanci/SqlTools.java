package caidanci;

import com.gluonhq.attach.storage.StorageService;
import com.gluonhq.attach.util.Platform;
import com.gluonhq.attach.util.Services;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Iammm 2024/8/24 12:46
 */
public class SqlTools {

    Connection connection;
    Statement statement;

    public SqlTools() {
        AtomicReference<String> dbPath = new AtomicReference<>(
                System.getProperty("user.dir") + "/caidanci.mv.db");
        var os = Platform.getCurrent();
        System.out.println(os.getName());
        if (os == Platform.ANDROID) {
            Services.get(StorageService.class).ifPresent(storage -> {
                File internalStorage = storage.getPrivateStorage().orElse(null);
                if (internalStorage != null) {
                    System.out.println("Internal Storage Path: " + internalStorage.getAbsolutePath());
                    dbPath.set(internalStorage.getAbsolutePath() + "caidanci.mv.db");
                } else {
                    System.out.println("Failed to get internal storage path.");
                }
            });
        }
        try {
            if (!new File(dbPath.get()).exists()) {
                System.out.println("数据库不存在，正在创建...");
                try (InputStream is = getClass().getResourceAsStream("/caidanci/caidanci.mv.db")) {
                    File outputFile = new File(dbPath.get());
                    System.out.println(outputFile.getAbsolutePath());
                    Files.copy(Objects.requireNonNull(is), outputFile.toPath(),
                            StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            Path res = Paths.get(dbPath.get());

            String h2Path = "jdbc:h2:" + res.toString().replace(".mv.db", "");

            System.out.println(dbPath);

            // 创建连接
            connection = DriverManager.getConnection(h2Path, "SA", "");
            statement = connection.createStatement();
            System.out.println("连接数据库成功！");
            getAnswer(5);
            System.out.println("this is check ");
        } catch (Exception e) {
            System.err.println("连接数据库失败：" + e.getMessage());
            e.printStackTrace();
        }
    }

    void close() {
        // 关闭连接
        try {
            if (connection != null && statement != null) {
                connection.close();
                statement.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        System.err.println("关闭数据库连接成功！");
    }

    String getAnswer(int limit) {
        String resultWord = "";
        try {
            String query = "SELECT word FROM \"EnWords\" WHERE LENGTH(word) = ? ORDER BY RANDOM() LIMIT 1";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, limit);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    resultWord = resultSet.getString("word");
                    System.out.println("随机选择的单词：" + resultWord);
                }
            }
        } catch (Exception e) {
            System.err.println("查询数据库失败：" + e.getMessage());
            e.printStackTrace();
        }
        return resultWord;
    }

    boolean checkWord(String word) {
        boolean isWord = false;
        try {
            String query = "SELECT COUNT(*) FROM \"EnWords\" WHERE word = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, word);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    isWord = resultSet.getInt(1) > 0;
                    System.out.println("单词 " + word + " 是否存在于数据库中：" + isWord);

                }
            }
        } catch (Exception e) {

            System.err.println("查询数据库失败：" + e.getMessage());
            e.printStackTrace();
        }
        return isWord;
    }

    public String getWordInfo(String answerWorld) {
        String resultWord = "";
        try {
            String query = "SELECT word, translation FROM \"EnWords\" WHERE word = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, answerWorld);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    resultWord = resultSet.getString("translation");
                }
            }
        } catch (Exception e) {
            System.err.println("查询数据库失败：" + e.getMessage());
            e.printStackTrace();
        }
        return resultWord;
    }
}
