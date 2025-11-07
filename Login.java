
import java.io.*;
import java.nio.file.*;
import java.security.MessageDigest;

import java.util.*;

public class Login {
    private static final String USER_FILE = "users.txt";

    
    private static Map<String, String[]> loadUsers() {
        Map<String, String[]> users = new LinkedHashMap<>();
        try {
            if (!Files.exists(Paths.get(USER_FILE))) return users;
            List<String> lines = Files.readAllLines(Paths.get(USER_FILE));
            for (String line : lines) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split("\\|");
                if (parts.length >= 4) {
                    users.put(parts[0], new String[]{parts[1], parts[2], parts[3]});
                }
            }
        } catch (IOException e) {
            System.out.println("Không thể đọc users.txt: " + e.getMessage());
        }
        return users;
    }

    private static void saveUsers(Map<String, String[]> users) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(USER_FILE))) {
            for (Map.Entry<String, String[]> e : users.entrySet()) {
                String name = e.getKey();
                String[] data = e.getValue();
                pw.println(name + "|" + data[0] + "|" + data[1] + "|" + data[2]);
            }
        } catch (IOException ex) {
            System.out.println("Lỗi khi lưu users.txt: " + ex.getMessage());
        }
    }

    
    private static String hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] b = md.digest(input.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte x : b) sb.append(String.format("%02x", x));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    
    private static String readPasswordPrompt(Scanner sc) {
        Console console = System.console();
        if (console != null) {
            char[] passwd = console.readPassword("Nhập mật khẩu: ");
            return new String(passwd);
        } else {
            System.out.print("Nhập mật khẩu (chú ý: trong VS Code mật khẩu có thể hiện ký tự): ");
            return sc.nextLine();
        }
    }

    
    public static String[] login() {
        Scanner sc = new Scanner(System.in, "UTF-8");
        Map<String, String[]> users = loadUsers();

        while (true) {
            System.out.print("Nhập tên người chơi: ");
            String name = sc.nextLine().trim();
            if (name.isEmpty()) continue;

            if (users.containsKey(name)) {
                System.out.println("Tài khoản tìm thấy. Vui lòng nhập mật khẩu.");
                String pass = readPasswordPrompt(sc);
                String hashed = hash(pass);
                if (hashed.equals(users.get(name)[0])) {
                    System.out.println("✅ Đăng nhập thành công. Xin chào " + name + "!");
                    return new String[]{name, users.get(name)[1], users.get(name)[2]};
                } else {
                    System.out.println("❌ Sai mật khẩu. Thử lại.");
                    
                }
            } else {
                System.out.print("Tài khoản chưa tồn tại. Tạo mới? (y/n): ");
                String c = sc.nextLine().trim();
                if (c.equalsIgnoreCase("y")) {
                    String pass = readPasswordPrompt(sc);
                    String hashed = hash(pass);
                    users.put(name, new String[]{hashed, "0", "0"});
                    saveUsers(users);
                    System.out.println("✅ Tạo tài khoản thành công. Xin chào " + name + "!");
                    return new String[]{name, "0", "0"};
                } else {
                    System.out.println("Quay lại đăng nhập...");
                }
            }
        }
    }

    
    public static void updateUser(String name, int correct, int money) {
        Map<String, String[]> users = loadUsers();
        if (!users.containsKey(name)) {
            // shouldn't happen, but create
            users.put(name, new String[]{"", String.valueOf(correct), String.valueOf(money)});
            saveUsers(users);
            return;
        }
        String[] data = users.get(name);
        int bestCorrect = Integer.parseInt(data[1]);
        int bestMoney = Integer.parseInt(data[2]);
        boolean changed = false;
        if (correct > bestCorrect) {
            data[1] = String.valueOf(correct);
            changed = true;
        }
        if (money > bestMoney) {
            data[2] = String.valueOf(money);
            changed = true;
        }
        if (changed) saveUsers(users);
    }
}
