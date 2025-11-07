// Main.java
import java.io.*;
import java.util.*;
// import java.time.LocalDateTime;
// import java.time.format.DateTimeFormatter;

public class Main {
    static Scanner sc = new Scanner(System.in, "UTF-8");

   
    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String YELLOW = "\u001B[33m";
    public static final String GREEN = "\u001B[32m";

   
    static final int[] REWARDS = {
        0, 200000, 400000, 600000, 1000000,
        2000000, 3000000, 6000000, 10000000, 14000000,
        22000000, 30000000, 40000000, 60000000, 85000000, 150000000
    };

    
    public static ArrayList<Question> loadQuestions(String filename) {
        ArrayList<Question> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "UTF-8"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] p = line.split("\\|");
                if (p.length >= 6) {
                    list.add(new Question(p[0], p[1], p[2], p[3], p[4], p[5].trim().charAt(0)));
                }
            }
        } catch (IOException e) {
            System.out.println("❌ Lỗi đọc file câu hỏi: " + e.getMessage());
        }
        Collections.shuffle(list); 
        return list;
    }

   
    public static void printOptions(String[] opts, boolean[] showMask) {
        char ch = 'A';
        for (int i = 0; i < 4; i++) {
            if (showMask == null || showMask[i]) {
                System.out.println(ch + ". " + opts[i]);
            } else {
                System.out.println(ch + ". ----");
            }
            ch++;
        }
    }

    public static void main(String[] args) {
        String[] player = Login.login(); 
        String playerName = player[0];
        int bestCorrect = Integer.parseInt(player[1]);
        int bestMoney = Integer.parseInt(player[2]);

        System.out.println(GREEN + "Xin chào " + playerName + "! Kỷ lục: " + bestCorrect + " câu, " + bestMoney + " VNĐ" + RESET);
        
        History.showHistory(playerName);
        System.out.println();

        ArrayList<Question> questions = loadQuestions("questions.txt");
        if (questions.isEmpty()) {
            System.out.println("Không có câu hỏi nào! Kiểm tra file questions.txt.");
            return;
        }

        boolean used50 = false, usedAudience = false, usedCall = false;
        int correctCount = 0;
        int money = 0;
        int maxQ = Math.min(15, questions.size());

        for (int i = 0; i < maxQ; i++) {
            Question q = questions.get(i);
            System.out.println(RED + "\nCâu " + (i + 1) + ": " + q.getQuestion() + RESET);
            boolean[] showMask = new boolean[]{true, true, true, true};
            printOptions(q.getOptions(), showMask);

            System.out.println();
            System.out.println(YELLOW + "Nhập A/B/C/D để trả lời hoặc chọn số để dùng trợ giúp:" + RESET);
            System.out.println(YELLOW + "1) 50/50   2) Hỏi khán giả   3) Gọi điện   0) Dừng chơi" + RESET);
            System.out.print("Lựa chọn: ");
            String choice = sc.nextLine().trim();
         
            if (choice.equals("1")) {
                if (used50) {
                    System.out.println("❌ Bạn đã dùng 50/50 rồi!");
                    i--;
                    continue;
                }
                used50 = true;
                boolean[] mask = Help.fiftyFiftyMask(q);
                printOptions(q.getOptions(), mask);
                System.out.print("Trả lời: ");
                String ans = sc.nextLine().trim().toUpperCase();
                if (ans.length() == 1 && ans.charAt(0) == q.getCorrect()) {
                    System.out.println(GREEN + "✅ Chính xác!" + RESET);
                    correctCount++;
                    money = REWARDS[correctCount];
                } else {
                    System.out.println("❌ Sai! Đáp án đúng: " + q.getCorrect());
                    break;
                }
            } 
            else if (choice.equals("2")) {
                if (usedAudience) {
                    System.out.println("❌ Bạn đã dùng hỏi khán giả rồi!");
                    i--;
                    continue;
                }
                usedAudience = true;
                int[] percent = Help.audiencePoll(q);
                System.out.println(YELLOW + "\n📊 Kết quả bình chọn:" + RESET);
                char cc = 'A';
                for (int p : percent) {
                    System.out.printf("%c: %d%%   ", cc, p);
                    cc++;
                }
                System.out.println();
                System.out.print("Trả lời: ");
                String ans = sc.nextLine().trim().toUpperCase();
                if (ans.length() == 1 && ans.charAt(0) == q.getCorrect()) {
                    System.out.println(GREEN + "✅ Chính xác!" + RESET);
                    correctCount++;
                    money = REWARDS[correctCount];
                } else {
                    System.out.println("❌ Sai! Đáp án đúng: " + q.getCorrect());
                    break;
                }
            } 
            else if (choice.equals("3")) {
                if (usedCall) {
                    System.out.println("❌ Bạn đã gọi điện rồi!");
                    i--;
                    continue;
                }
                usedCall = true;
                String[] friends = {"bạn thân", "mẹ", "anh", "chị", "bác"};
                String who = friends[new Random().nextInt(friends.length)];
                char suggest = Help.callFriendSuggest(q);
                System.out.println(YELLOW + "📞 " + who + " nghĩ đáp án là: " + suggest + RESET);
                System.out.print("Trả lời: ");
                String ans = sc.nextLine().trim().toUpperCase();
                if (ans.length() == 1 && ans.charAt(0) == q.getCorrect()) {
                    System.out.println(GREEN + "✅ Chính xác!" + RESET);
                    correctCount++;
                    money = REWARDS[correctCount];
                } else {
                    System.out.println("❌ Sai! Đáp án đúng: " + q.getCorrect());
                    break;
                }
            } 
            else if (choice.equals("0")) {
                System.out.println("🟡 Bạn đã dừng chơi. Bạn nhận: " + money + " VNĐ");
                break;
            } 
            else {
                String ans = choice.trim().toUpperCase();
                if (ans.length() == 1 && ans.charAt(0) == q.getCorrect()) {
                    System.out.println(GREEN + "✅ Chính xác!" + RESET);
                    correctCount++;
                    money = REWARDS[correctCount];
                } else {
                    System.out.println("❌ Sai! Đáp án đúng: " + q.getCorrect());
                    break;
                }
            }

            if (i == maxQ - 1) {
                System.out.println(GREEN + "\n🎉 Bạn đã hoàn thành tất cả câu hỏi!" + RESET);
            }
        }

       
        System.out.println("\n===== KẾT QUẢ =====");
        System.out.println("Người chơi: " + playerName);
        System.out.println("Số câu đúng: " + correctCount);
        System.out.println("Tiền thưởng: " + money + " VNĐ");

     
        Login.updateUser(playerName, correctCount, money);
        History.addRecord(playerName, correctCount, money);
        System.out.println("✅ Kết quả đã được lưu!");
    }
}
