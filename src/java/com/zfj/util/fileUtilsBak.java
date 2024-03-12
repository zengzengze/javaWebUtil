package com.zfj.util;


import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;

public class fileUtilsBak {

    private static final String path = "H:/zfj/Code/ms/";

    private static String[] head = new String[]{"usr_bran", "usr_nm", "inst_pnp_id", "sex", "chanl_no", "usr_lev"};
    private static String[] chanlInf = new String[]{"短信", "渠道平台", "企业网银参数管理", "社交网络媒体渠道", "渠道欺诈侦测平台"};
    private static String[] chanlNos = new String[]{"05", "00", "15", "37", "12"};

    private static String[] usr_bran = new String[]{"总行", "包头分行", "巴彦淖尔分行","阿拉善分行","兴安盟分行","赤峰分行","鄂尔多斯分行",
            "二连浩特分行","呼伦贝尔分行","满洲里分行","通辽分行","乌兰察布分行","总行营业部","乌海分行","呼和浩特分行","锡林郭勒分行"};
    private static String[] usr_bran_nos = new String[]{"111111111", "200010000", "800030000","901160000","201120000","400020000","600060000",
            "501190000","700090000","781180000","500040000","900130000","200000200","300150000","100110000","501070000"};

    private static String[] usr_lev_nm05 = new String[]{"业务操作员", "后台管理员", "查询用户"};
    private static String[] usr_lev_05 = new String[]{"B", "C", "D"};

    private static String[] usr_lev_nm00 = new String[]{"主管", "管理员", "操作员", "查询用户"};
    private static String[] usr_lev_00 = new String[]{"B", "C", "D", "E"};

    private static String[] usr_lev_nm15 = new String[]{"WEB高级操作员", "WEB普通操作员"};
    private static String[] usr_lev_15 = new String[]{"I", "J"};

    private static String[] usr_lev_nm37 = new String[]{"审核员", "业务主管", "操作员", "系统管理员", "蒙商公众号"};
    private static String[] usr_lev_37 = new String[]{"A", "B", "C", "D", "H"};

    private static String[] usr_lev_nm12 = new String[]{"总行主管岗位", "总行处理岗位", "总行受理岗位", "分控运营岗", "风控专员岗", "总行质检岗", "交易分析岗", "运营支持岗"};
    private static String[] usr_lev_12 = new String[]{"1", "2", "3", "4", "5", "6", "7", "8"};

    private static String sql="update USR_MGR_INF set PURV_BTMP=(select purv_btmp from usr_lev_purv where bran_mgr_lev in ('0') and usr_lev in ('B') and mgr_bran in ('001') and trim(bran_no) is null) where chanl_no ='00' and bran_mgr_lev in ('0') and usr_lev in ('B','C');\n" +
            "update USR_MGR_INF set PURV_BTMP=(select purv_btmp from usr_lev_purv where bran_mgr_lev in ('0') and usr_lev in ('D') and mgr_bran in ('001') and trim(bran_no) is null) where chanl_no ='00' and bran_mgr_lev in ('0') and usr_lev in ('D');\n" +
            "update USR_MGR_INF set PURV_BTMP=(select purv_btmp from usr_lev_purv where bran_mgr_lev in ('0') and usr_lev in ('E') and mgr_bran in ('001') and trim(bran_no) is null) where chanl_no ='00' and bran_mgr_lev in ('0') and usr_lev in ('E');\n" +
            "\n" +
            "update USR_MGR_INF set PURV_BTMP=(select purv_btmp from usr_lev_purv where bran_mgr_lev in ('1') and usr_lev in ('B') and mgr_bran in ('001') and trim(bran_no) is null) where chanl_no ='00' and bran_mgr_lev in ('1') and usr_lev in ('B');\n" +
            "update USR_MGR_INF set PURV_BTMP=(select purv_btmp from usr_lev_purv where bran_mgr_lev in ('1') and usr_lev in ('E') and mgr_bran in ('001') and trim(bran_no) is null) where chanl_no ='00' and bran_mgr_lev in ('1') and usr_lev in ('E');\n";

    public static void main(String[] args) throws Exception {
        String xlsxPath = "H:\\zfj\\蒙商\\初始化数据\\用户初始化数据-uat.xlsx";
        XSSFWorkbook xssfWorkbook = new XSSFWorkbook(new FileInputStream(xlsxPath));
        XSSFSheet sheetAt = xssfWorkbook.getSheetAt(1);
        int maxRow = sheetAt.getLastRowNum();
        Class clazz = Class.forName("com.zfj.util.fileUtilsBak");
        HashMap[] maps = new HashMap[maxRow];
        for (int row = 1; row < (maxRow + 1); row++) {
            int maxRol = sheetAt.getRow(row).getLastCellNum();
            HashMap<String, String> map = new HashMap<String, String>();
            String bran_no = sheetAt.getRow(row).getCell(0).toString();
            for (int col = 0; col < maxRol; col++) {
                String s = sheetAt.getRow(row).getCell(col).toString();
                if (col == 0) {
                    for (int i = 0; i < usr_bran.length; i++) {
                        if (s.equals(usr_bran[i])) {
                            s = usr_bran_nos[i];
                            break;
                        }
                    }
                }
                if (col == 4) {
                    for (int i = 0; i < chanlInf.length; i++)
                        if (s.equals(chanlInf[i])) {
                            s = chanlNos[i];
                            break;
                        }
                }
                map.put(head[col], s);
            }
            maps[row -1] = map;
        }
        HashMap<String, HashMap<String, Integer>> usrBran = new HashMap<String, HashMap<String, Integer>>();
        HashMap<String, Boolean> isTrue = new HashMap<String, Boolean>();
        StringBuffer usrInfoSqls = new StringBuffer("");
        StringBuffer usrMgrInfSqls = new StringBuffer("");
        StringBuffer usrInfoSqls_p8 = new StringBuffer("");
        StringBuffer usrMgrInfSqls_p8 = new StringBuffer("");
        StringBuffer delSql = new StringBuffer("");
        for (HashMap<String, String> map : maps) {
            if (map == null) continue;
            String usr_bran = map.get("usr_bran");
            String inst_pnp_id = map.get("inst_pnp_id");
            String usr_nm = map.get("usr_nm");
            String chanl_no = map.get("chanl_no");
            String usr_lev = map.get("usr_lev");
            usrBran.computeIfAbsent(usr_bran, k -> new HashMap<String, Integer>());
            HashMap<String, Integer> hashMap = usrBran.get(usr_bran);
            if (null == hashMap.get(inst_pnp_id)) {
                hashMap.put(inst_pnp_id, hashMap.size() + 1);
                usrBran.put(usr_bran, hashMap);
            }
            Object len = usrBran.get(usr_bran).get(inst_pnp_id);
            int endIndex = usr_bran.length() - len.toString().length();
            String bran_mgr_lev = !"111111111".equals(usr_bran) ? "1" : "0";
            usr_bran = "111111111".equals(usr_bran) ? "999999999" : usr_bran;
            String usr_no = usr_bran.substring(0, endIndex) + len;
            if ("999999999".equals(usr_bran)) usr_no = "999990000".substring(0, endIndex) + len;
            Field field = clazz.getDeclaredField("usr_lev_nm" + chanl_no);
            field.setAccessible(true);
            String[] usr_lev_nms = (String[]) field.get(clazz);
            for (int i = 0; i < usr_lev_nms.length; i++)
                if (usr_lev.equals(usr_lev_nms[i])) {
                    field = clazz.getDeclaredField("usr_lev_" + chanl_no);
                    String[] usr_levs = (String[]) field.get(clazz);
                    usr_lev = usr_levs[i];
                    break;
                }
            delSql.append(",'").append(usr_no).append("'");
            String sql = "insert into USR_MGR_INF(usr_no,chanl_no,usr_nm,mgr_bran,usr_typ,bran_mgr_lev,usr_lev,purv_btmp,opt_usr,opt_date,rec_sts,tran_pswd) " +
                    "values ('" + usr_no + "','" + chanl_no + "','" + usr_nm + "','001',null,'" + bran_mgr_lev + "','" + usr_lev + "','',null,null,'0','6FA89230D67A4E8FC60A49D78A2DBB975A675468674F');\r\n";
            String sql_p8 = "insert into USR_MGR_INF(usr_no,chnl_tpcd,usr_nm,usr_spvs_dept_dsc,usr_tpds,inst_mgt_lvl_dsc,usr_lvl_dsc,usr_ahr_bmp_dsc,add_usr_no,add_dt,usr_rcrd_st,web_bkstg_txn_pswd)  " +
                    "values ('" + usr_no + "','" + chanl_no + "','" + usr_nm + "','001','','" + bran_mgr_lev + "','" + usr_lev + "','','330000002','20231115','0','6FA89230D67A4E8FC60A49D78A2DBB975A675468674F');\r\n";
            if (null == isTrue.get(inst_pnp_id)) {
                String sql1 = "insert into USR_INFO(usr_no,nickname,usr_bran,usr_pswd,usr_nm,sex,birday,posn,dept,degr_id,work_id,work_tel,mobil_no,tel_no,home_addr,email_addr,reg_flg,lst_pswd_chg,opt_usr,opt_date,rec_sts,pswd_wrg_tms,inst_pnp_id,ccbins_id)  " +
                        "values ('" + usr_no + "','" + usr_nm + "','" + usr_bran + "','6FA89230D67A4E8FC60A49D78A2DBB975A675468674F','" + usr_nm + "','','','','','','','','00000000000','','','','C','','','20231108','0',0,'" + inst_pnp_id + "','" + usr_bran + "');\r\n";
                String sql1_p8 = "insert into USR_INFO(usr_no,usr_shrtnm,usr_insid,usr_pswd,usr_nm,usr_gnd_dsc,brth_yrmo,post_dsc,dept_dsc,idcard_no,wrk_crtfno,wrk_tel,move_telno,rsdnc_tel,rsdnc_adr,email_adr,land_mod_dsc,pswd_lsttm_mod_dt,add_usr_no,add_dt,usr_rcrd_st,pswd_err_cnt,inst_pnp_id,ccbins_id)  " +
                        "values ('" + usr_no + "','','" + usr_bran + "','6FA89230D67A4E8FC60A49D78A2DBB975A675468674F','" + usr_nm + "','M','19870829','','','','','010-88888888','18022000707','','','','A','20230915','999990002','20230915','0',0,'" + inst_pnp_id + "','" + usr_bran + "');\r\n";
                usrInfoSqls.append(sql1);
                usrInfoSqls_p8.append(sql1_p8);
                isTrue.put(inst_pnp_id, false);
            }
            usrMgrInfSqls.append(sql);
            usrMgrInfSqls_p8.append(sql_p8);
        }
        String delSql1 = "delete from usr_info where usr_no in (" + delSql.substring(1) + ");"
                + "\r\ndelete from usr_mgr_inf where usr_no in (" + delSql.substring(1) + ");";
        usrInfoSqls.append("\r\n").append(usrMgrInfSqls);
        usrInfoSqls_p8.append("\r\n").append(usrMgrInfSqls_p8);
        createFile(path, delSql1 + "\r\n" + usrInfoSqls.toString() + "\r\n" + sql, "ccst1-2_ccsht", ".sql");
        createFile(path, usrInfoSqls_p8.toString(), "elb_elbht", ".sql");
    }


    private static void createFile(String filePath, String str, String fileName, String stffx) throws IOException {
        FileWriter fileWriter = null;
        try {
            File f = new File(filePath);
            if (!f.exists()) f.mkdirs();
            fileName = filePath + fileName + stffx;
            File file = new File(fileName);
            if (file.exists()) file.delete();
            file.createNewFile();
            fileWriter = new FileWriter(fileName, true);
            fileWriter.write(str);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            if (fileWriter != null) fileWriter.close();
        }
    }

    private String chanlNo;
    private String chanlName;
    private String usrLev;
    private String usrLevDes;


}
