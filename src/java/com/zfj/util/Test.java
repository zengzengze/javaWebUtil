package com.zfj.util;

public class Test {

    String Test = "test555555";

    public static String[] usr_lev_nm05 = new String[]{"业务操作员", "后台管理员", "查询用户"};
    private static String[] usr_lev_05 = new String[]{"B", "C", "D"};

    private static String[] usr_lev_nm00 = new String[]{"主管", "管理员", "操作员", "查询用户"};
    private static String[] usr_lev_00 = new String[]{"B", "C", "D", "E"};

    private static String[] usr_lev_nm15 = new String[]{"WEB高级操作员", "WEB普通操作员"};
    private static String[] usr_lev_15 = new String[]{"I", "J"};

    private static String[] usr_lev_nm37 = new String[]{"审核员", "业务主管", "操作员", "系统管理员", "蒙商公众号"};
    private static String[] usr_lev_37 = new String[]{"A", "B", "C", "D", "H"};

    private static String[] usr_lev_nm12 = new String[]{"总行主管岗位", "总行处理岗位", "总行受理岗位", "分控运营岗", "风控专员岗", "总行质检岗", "交易分析岗", "运营支持岗"};
    private static String[] usr_lev_12 = new String[]{"1", "2", "3", "4", "5", "6", "7", "8"};

//    public static void main(String[] args) throws Exception{
//        Class clazz = Class.forName("com.zfj.util.httpUtilsQueryInterface");
//
////        Field field = clazz.getDeclaredField("usr_lev_nm" + chanl_no);
////        field.setAccessible(true);  //private
////        String[] usr_lev_nms = (String[]) field.get(clazz);
//
////        Method getValue = clazz.getDeclaredMethod("getValue", String.class);
////        System.out.println(getValue.invoke(String.class,"A0338Q529"));
//
////        Constructor<httpUtilsQueryInterface> constructor = clazz.getDeclaredConstructor(String.class, Integer.class);
////        constructor.setAccessible(true);
////        httpUtilsQueryInterface httpUtil = constructor.newInstance("zfj", 23);
////        String name = httpUtil.name;
////        Integer age = httpUtil.age;
//
//        Constructor<httpUtilsQueryInterface> constructor = clazz.getConstructor();
//        constructor.setAccessible(true);
//        httpUtilsQueryInterface httpUtil = constructor.newInstance();
//
//        Field nameField = clazz.getDeclaredField("name");
//        Field ageField = clazz.getDeclaredField("age");
//        nameField.set(httpUtil,"zfj");
//        ageField.set(httpUtil,23);
//
//        System.out.println(httpUtil.name+httpUtil.age);
//    }

    public static void main(String[] args) throws Exception {
        new fileUtils_ns();
    }


    public static void test() {
        String str = "1@3@3@2@1@3@1#\n" +
                "2@3@3@2@1@3@1#\n" +
                "6@3@3@2@0@3@0#\n" +
                "7@3@3@2@0@3@0#\n" +
                "93@3@3@2@1@3@1#\n" +
                "48@3@3@1@1@3@1#\n" +
                "49@1@1@1@1@1@1#\n" +
                "16@3@3@2@1@1@1#\n" +
                "17@3@3@2@1@1@1#\n" +
                "18@3@3@2@1@1@1#\n" +
                "19@3@3@2@1@1@1#\n" +
                "20@3@3@2@1@1@1#\n" +
                "21@3@3@2@1@1@1#\n" +
                "23@1@1@1@1@1@1#\n" +
                "273@1@1@1@1@1@1#\n" +
                "155@1@1@1@1@1@1#\n" +
                "513@1@1@1@1@1@1#\n" +
                "514@3@3@2@1@1@1#\n" +
                "267@1@1@1@1@1@1#\n" +
                "268@3@3@2@1@1@1#\n" +
                "269@3@3@2@1@1@1#\n" +
                "582@1@1@1@1@1@1#\n" +
                "277@1@1@1@1@1@1#\n" +
                "187@3@3@2@1@1@1#\n" +
                "188@3@3@2@1@1@1#\n" +
                "189@3@3@2@1@1@1#\n" +
                "191@1@1@1@1@1@1#\n" +
                "28@3@3@2@1@1@1#\n" +
                "27@3@3@2@1@1@1#\n" +
                "631@3@3@2@1@1@1#\n" +
                "684@3@3@2@1@1@1#\n" +
                "799@3@3@2@1@1@1#\n" +
                "675@3@3@2@1@1@1#\n" +
                "676@1@1@1@1@1@1#\n" +
                "493@3@3@1@1@1@1#\n" +
                "510@1@1@1@1@1@1#\n" +
                "86@3@3@2@1@1@1#\n" +
                "685@3@3@2@1@1@1#\n" +
                "493@3@3@1@1@1@1#\n" +
                "510@1@1@1@1@1@1#\n" +
                "611@3@3@2@1@1@1#\n" +
                "943@1@1@1@1@1@1#\n" +
                "536@3@3@2@1@1@1#\n" +
                "537@3@3@2@1@1@1#\n" +
                "223@3@3@2@1@1@1#\n"+
                "669@3@3@2@1@1@1#\n";
        String[][] strings = new String[6][1024];
        String[] split = str.split("#");
        for (int i = 0; i < split.length; i++) {
            String[] split1 = split[i].split("@");
            for (int j = 1; j < split1.length; j++) {
                int index = Integer.parseInt(split1[0].trim());
                strings[j - 1][index - 1] = split1[j];
            }
        }
        for (int i = 0; i < 13; i++) {
            for (int j = 0; j < 1024; j++) {
                if (strings[i][j] == null) {
                    strings[i][j] = String.valueOf(0);
                }
                System.out.print(strings[i][j]);
            }
            System.out.println();
        }

    }
}
