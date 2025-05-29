package com.back;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class App {
    Scanner sc = new Scanner(System.in);
    String command = "";
    String name = "";
    String word = "";
    int idx = -1;
    int ct = 1;

    List<Writer> arr = new ArrayList<>();

    public void run() {
        //파일 초기화
        resetFile();

        System.out.println("== 명언 앱 ==");
        while (!command.equals("종료")) {
            System.out.print("명령) ");
            command = sc.nextLine();

            //등록
            if (command.equals("등록")) {
                Map<String, String> data = new HashMap<>();

                System.out.print("명언 : ");
                word = sc.nextLine();
                System.out.print("작가 : ");
                name = sc.nextLine();

                //리스트에 저장
                arr.add(new Writer(ct, name, word));
                //파일에 저장
                saveFile(ct, name, word);

                System.out.printf("%d번 명언이 등록되었습니다.\n", ct);
                ct++;
            }

            //목록
            if (command.equals("목록")) {
                System.out.println("번호 / 작가 / 명언");
                System.out.println("----------------------");
                for(int i = arr.size() - 1 ; i >= 0; i--) {
                    System.out.printf("%d / %s / %s\n", arr.get(i).getIdx(), arr.get(i).getName(), arr.get(i).getWord());
                }
            }

            //삭제
            if (command.startsWith("삭제")) {
                idx = check_input(command);
                if(idx != -1)
                    del(arr, idx, ct);
            }

            //수정
            if (command.startsWith("수정")){
                idx = check_input(command);
                if(idx != -1)
                    modify(arr, idx);

            }

            //빌드
            if(command.equals("빌드")){
                writeBuildFile(arr);
            }

        }
    }

    //명령어 유효성 검사
    static int check_input(String command) {
        if (command.contains("?") && command.contains("=")) {
            // [명령] 과 id=[idx]를 ? 기준 분리
            String[] parts = command.split("\\?");
            //id=[idx] 를 = 부호 기준 분리
            String[] param = parts[1].split("=");
            if (param.length == 2 && param[0].equals("id")) {
                try {
                    return Integer.parseInt(param[1]);
                } catch (NumberFormatException e) {
                    System.out.println("숫자 형식 잘못 입력했어요");
                    return -1;
                }
            } else {
                System.out.println("파라미터 형식 잘못 입력했어요");
                return -1;
            }
        } else {
            System.out.println("명령어 오타났어요!");
            return -1;
        }
    }

    //등록
    static void register(){}

    //파일 내용 작성
    static String writeFile(int idx, String name, String word) {
        String s = "  {\n"
                + "     \"id\": \"" + idx + "\",\n"
                + "     \"word\": \"" + word + "\",\n"
                + "     \"name\": \"" + name + "\"\n"
                + "   }";
       return s;
    }

    //삭제
    static void del(List<Writer> arr, int idx, int ct) {
        for (int i = 0; i < arr.size(); i++) {
            if (arr.get(i).getIdx() == idx) {
                arr.remove(i);
                System.out.printf("%d번 명언이 삭제되었습니다.\n", idx);

                File dir = new File("db/wiseSaying/");
                File file = new File(dir, idx + ".json");
                System.out.printf("%d %d\n",idx, ct);
                //마지막 파일 삭제 시 lastId 수정
                if (idx + 1 == ct) {
                    if(i > 0)
                        writeLastId(arr.get(i - 1).getIdx());
                    else
                        writeLastId(-1);
                }

                file.delete();
                return;
            }
        }
        System.out.printf("%d번 명언은 존재하지 않습니다.\n", idx);
    }

    //수정
    static void modify(List<Writer> arr, int idx) {
        Scanner sc = new Scanner(System.in);
        for (Writer w : arr) {
            if (w.getIdx() == idx) {
                System.out.println("명언(기존) : " + w.getWord());
                System.out.print("명언 : ");
                w.setWord(sc.nextLine());

                System.out.println("작가(기존) : " + w.getName());
                System.out.print("작가 : ");
                w.setName(sc.nextLine());
                return;
            }
        }
        System.out.printf("%d번 명언은 존재하지 않습니다.\n", idx);
    }


    //파일 탐색
    static File findFile(int idx) {
        File dir = new File("db/wiseSaying/");
        File[] files = dir.listFiles();
        for (File f : files) {
            if (f.getName().equals(idx+".json"))
                return f;
        }
        return null;
    }

    //파일 초기화
    static void resetFile() {
        File dir = new File("db/wiseSaying/");
        if (!dir.exists())
            dir.mkdirs();

        //dir 디렉토리의 파일 초기화
        File[] files = dir.listFiles();
        for (int i = 0; i < files.length; i++) {
            files[i].delete();
        }
    }

    //파일 생성
    static void saveFile(int idx, String name, String word) {
        File dir = new File("db/wiseSaying/");

        String fileName = idx + ".json";
        File file = new File(dir, fileName);
        String s = writeFile(idx, name, word);

        try (FileWriter writer = new FileWriter(file)) {
            writer.write(s);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //lastId 파일 생성
        writeLastId(idx);
    }

    //파일 삭제
    static void deleteFile(int idx) {
        File file = findFile(idx);
        if(file != null){
            file.delete();
            System.out.println("삭제됨");
        }
    }

    //lastId 생성
    static void writeLastId(int idx){
        String fileName = "lastId.txt";
        String dir = "db/wiseSaying/";
        File file = new File(dir, fileName);

        try (FileWriter writer = new FileWriter(file)) {
            if(idx > 0)
                writer.write(String.valueOf(idx));
            else
                writer.write("");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //빌드 파일 생성
    static void writeBuildFile(List<Writer> arr) {
        File dir = new File("db/wiseSaying/");

        String fileName = "data.json";
        File file = new File(dir, fileName);
        String s = "";
        if(arr.size() > 0)
             s+="[\n";

        for(int i = 0; i < arr.size(); i++) {
            s+=writeFile(arr.get(i).getIdx(), arr.get(i).getName(), arr.get(i).getWord());
            if(i < arr.size() - 1)
                s+=",\n";
        }
        s+="\n]";

        try (FileWriter writer = new FileWriter(file)) {
            writer.write(s);
            System.out.println("data.json 파일의 내용이 갱신되었습니다.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
