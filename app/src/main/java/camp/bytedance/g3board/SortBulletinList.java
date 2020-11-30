package camp.bytedance.g3board;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author Bytedance Technical Camp, Client Group 3, 吴迪 & 王龙逊
 * @date 2020/11/30
 * @description 用于给bulletinList排序
 * order == 1 时间倒序
 * order == -1 时间顺序
 */

public class SortBulletinList {

    public static List<Bulletin> sort(List<Bulletin> bulletinList, int order) {
        List<Bulletin> sortList = new ArrayList<>(bulletinList);
        Collections.sort(sortList, new Comparator<Bulletin>() {
            @Override
            public int compare(Bulletin o1, Bulletin o2) {
                char[] time1 = o1.getTime().toCharArray();
                char[] time2 = o2.getTime().toCharArray();
                for (int i = 0; i < time1.length; i++) {
                    if(isChineseChar(time1[i])) {
                        time1[i] = ',';
                    }
                }
                for (int i = 0; i < time2.length; i++) {
                    if(isChineseChar(time2[i])) {
                        time2[i] = ',';
                    }
                }
                String[] s1 = new String(time1).split(",");
                String[] s2 = new String(time2).split(",");
                for (int i = 0; i < Math.min(s1.length, s2.length); i++) {
                    if(s1[i].length() == s2[i].length()) {
                        if (s1[i].equals(s2[i])) {
                            continue;
                        } else  {
                            return s1[i].compareTo(s2[i]) * order;
                        }
                    } else {
                        return (s1[i].length() - s2[i].length()) * order;
                    }
                }
                return order;
            }
        });
        return sortList;
    }

    /**辅助方法，判断字符是否为中文，对公告列表进行排序时使用**/
    private static boolean isChineseChar(char c) {
        try {
            return String.valueOf(c).getBytes("UTF-8").length > 1;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return false;
        }
    }
}
