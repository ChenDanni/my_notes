import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Combine {

    static HashSet<Integer> otherSet = new HashSet<>();
    static List<Integer> otherNums = new ArrayList<>();
    static List<String> otherNames = new ArrayList<>();
    static List<String> diffs = new ArrayList<>();
    public static String getSubUtilSimple(String soap,String rgex, int n){
        boolean isMatch = Pattern.matches(rgex, soap);

        Pattern pattern = Pattern.compile(rgex);// 匹配的模式
        Matcher m = pattern.matcher(soap);

        while(m.find()){
            return m.group(n);
        }
        return "";
    }

    public static void combine(HashSet<Integer> acNumSet, List<Integer> acNums, List<String> acNames, String sessionPath) {
        try {
            String encoding = "GBK";
            File file = new File(sessionPath);

            if (file.isFile() && file.exists()) { // 判断文件是否存在
                InputStreamReader read = new InputStreamReader(
                        new FileInputStream(file), encoding);// 考虑到编码格式
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;


                while ((lineTxt = bufferedReader.readLine()) != null) {
                    if (lineTxt.equals("<tr>")) {
                        StringBuilder sb = new StringBuilder();
                        while ((lineTxt = bufferedReader.readLine()) != null && !(lineTxt.equals("</tr>"))) sb.append(lineTxt);
                        String curr = sb.toString();
                        // get number
                        String numRgex = "\\<td label\\=\"\\[object Object\\]\"\\>(\\d+)\\</td\\>";
                        int num = Integer.valueOf(getSubUtilSimple(curr, numRgex, 1));
                        // get name
                        String nameRgex = "\\<td label\\=\"\\[object Object\\]\"\\>(\\d+)\\</td\\>    \\<td value\\=\"(.+)\" label=\"\\[object Object\\]\"\\>\\s+\\<div\\>\\<a href";
                        String name = getSubUtilSimple(curr, nameRgex, 2);

                        if (!otherSet.contains(num)) {
                            String diffRegx = "\\<span class\\=\"label label-.* round\"\\>(.+)\\</span\\>";
                            String diff = getSubUtilSimple(curr, diffRegx, 1);

                            otherSet.add(num);
                            otherNums.add(num);
                            otherNames.add(name);
                            diffs.add(diff);
                        }

                        if (curr.contains("value=\"ac\"")) {
                            if (!acNumSet.contains(num)) {
                                acNumSet.add(num);
                                acNums.add(num);
                                acNames.add(name);
                            }
                        }
                    }
                }
                bufferedReader.close();
                read.close();
            } else {
                System.out.println("找不到指定的文件");
            }
        } catch (Exception e) {
            System.out.println("读取文件内容出错");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        HashSet<Integer> acNumSet = new HashSet<>();

        List<Integer> acNums = new ArrayList<>();
        List<String> acNames = new ArrayList<>();


        String[] sessionPaths = {
                "/Users/chenche/Documents/github/my_notes_and_utilities/utilities/clawer_helper/src/session1.html",
                "/Users/chenche/Documents/github/my_notes_and_utilities/utilities/clawer_helper/src/session2.html",
                "/Users/chenche/Documents/github/my_notes_and_utilities/utilities/clawer_helper/src/session3.html"
        };

        for (String path : sessionPaths) {
            combine(acNumSet, acNums, acNames, path);
        }

        List<Integer> undoNum = new ArrayList<>();
        List<String> undoName = new ArrayList<>();
        List<String> undoDiff = new ArrayList<>();


        for (int i = 1;i <= 968;i++) {
            if (!acNumSet.contains(i) && otherSet.contains(i)) {
                int idx = otherNums.indexOf(i);
                undoNum.add(i);
                undoName.add(otherNames.get(idx));
                undoDiff.add(diffs.get(idx));
            }
        }

//        for (int n : undoNum) System.out.println(n);
//        for (String s : undoName) System.out.println(s);
//        for (String s : undoDiff) System.out.println(s);
    }

}
