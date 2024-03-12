package com.zfj.util;


import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class fileUtils_ns {

    private static final String path = "H:/zfj/ns/";
    private static String xlsxPath = "C:\\Users\\28065\\Desktop\\南商\\NCB.xlsx";

    private static StringBuffer usrInfoSqls_p1 = new StringBuffer("");
    private static StringBuffer usrInfoSqls_p8 = new StringBuffer("");
    private static StringBuffer usrMgrInfSqls_p1 = new StringBuffer("");
    private static StringBuffer usrMgrInfSqls_p8 = new StringBuffer("");

    private static HashMap<String, HashMap> chanlInfoMap = new HashMap<String, HashMap>();
    private static HashMap<String, String> usrBrans = new HashMap<String, String>();
    private static XSSFWorkbook xssfWorkbook;
    private static String[] head = {"usr_bran", "usr_nm", "inst_pnp_id", "sex", "chanl_no", "usr_lev", "mgr_bran"};

    private static String[][] usr_brans = {
            {"总行"},
            {"111111111"}};

    static {
        try {
            xssfWorkbook = new XSSFWorkbook(new FileInputStream(xlsxPath));
            chanlInfoMapInit();
            usrBransInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        createInitSql();
    }

    public static void createInitSql() throws Exception {
        XSSFSheet sheetAt = xssfWorkbook.getSheetAt(1);
        int maxRow = sheetAt.getLastRowNum();
        HashMap<String, String>[] maps = new HashMap[maxRow];
        for (int row = 1; row < (maxRow + 1); row++) {
            int maxRol = sheetAt.getRow(row).getLastCellNum();
            HashMap<String, String> map = new HashMap<String, String>();
            for (int col = 0; col < maxRol; col++) {
                String s = sheetAt.getRow(row).getCell(col).toString();
                if (col == 4) {
                    HashMap chanlMap = chanlInfoMap.get(s);
                    String usrLev = sheetAt.getRow(row).getCell(col + 1).toString();
                    fileUtils_ns chanlDetail = (fileUtils_ns) chanlMap.get(usrLev);
                    map.put(head[col], chanlDetail.getChanlNo());
                    map.put(head[++col], chanlDetail.getUsrLev());
                    continue;
                }
                map.put(head[col], s);
            }
            maps[row - 1] = map;
        }
        HashMap<String, HashMap<String, Integer>> usrBran = new HashMap<String, HashMap<String, Integer>>();
        List<String> existEmployeeIdList = new ArrayList<>();
        for (HashMap<String, String> usrInfoMap : maps) {
            if (usrInfoMap == null) continue;
            String usr_bran = usrBrans.get(usrInfoMap.get("usr_bran"));
            String inst_pnp_id = usrInfoMap.get("inst_pnp_id");
            String usr_nm = usrInfoMap.get("usr_nm");
            String chanl_no = usrInfoMap.get("chanl_no");
            String usr_lev = usrInfoMap.get("usr_lev");
            String mgr_bran = usrInfoMap.get("mgr_bran");
            String sex = "男".equals(usrInfoMap.get("sex")) ? "M" : "F";

            usrBran.computeIfAbsent(usr_bran, k -> new HashMap<String, Integer>());
            HashMap<String, Integer> hashMap = usrBran.get(usr_bran);
            if (null == hashMap.get(inst_pnp_id)) {
                hashMap.put(inst_pnp_id, hashMap.size() + 1);
                usrBran.put(usr_bran, hashMap);
            }
            String len = String.valueOf(usrBran.get(usr_bran).get(inst_pnp_id));
            int endIndex = usr_bran.length() - len.length();
            String bran_mgr_lev = !"111111111".equals(usr_bran) ? "1" : "0";
            usr_bran = "111111111".equals(usr_bran) ? "999999999" : usr_bran;
            String usr_no = usr_bran.substring(0, endIndex) + len;
            if ("999999999".equals(usr_bran)) usr_no = "999991000".substring(0, endIndex) + len;

            if ("对公+对私".equals(mgr_bran)) {
                createUsrMgrInfSql(usr_no, chanl_no, usr_nm, "001", bran_mgr_lev, usr_lev);
                createUsrMgrInfSql(usr_no, chanl_no, usr_nm, "002", bran_mgr_lev, usr_lev);
            } else if ("对私".equals(mgr_bran)) {
                createUsrMgrInfSql(usr_no, chanl_no, usr_nm, "001", bran_mgr_lev, usr_lev);
            } else if ("对公".equals(mgr_bran)) {
                createUsrMgrInfSql(usr_no, chanl_no, usr_nm, "002", bran_mgr_lev, usr_lev);
            }
            if (!existEmployeeIdList.contains(inst_pnp_id)) {
                createUsrInfoSql(usr_no, usr_bran, usr_nm, sex, inst_pnp_id);
                existEmployeeIdList.add(inst_pnp_id);
            }
        }
        usrInfoSqls_p1.append("\r\n").append(usrMgrInfSqls_p1);
        usrInfoSqls_p8.append("\r\n").append(usrMgrInfSqls_p8);
        createFile(path, usrInfoSqls_p1.toString(), "ccst1-2_ccsht", ".sql");
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

    private static void usrBransInit() {
        for (int i = 0; i < usr_brans[0].length; i++) {
            usrBrans.put(usr_brans[0][i], usr_brans[1][i]);
        }
    }

    private static void chanlInfoMapInit() {
        XSSFSheet sheetAt3 = xssfWorkbook.getSheetAt(3);
        int maxRow3 = sheetAt3.getLastRowNum();
        for (int i = 1; i < maxRow3 + 1; i++) {
            String chanl_nm3 = sheetAt3.getRow(i).getCell(1).toString();
            XSSFSheet sheetAt2 = xssfWorkbook.getSheetAt(2);
            int maxRow2 = sheetAt2.getLastRowNum();
            HashMap<String, fileUtils_ns> chanlMap = new HashMap<>();
            for (int row = 1; row < maxRow2 + 1; row++) {
                fileUtils_ns chanlDetail = new fileUtils_ns();
                String chanl_nm = sheetAt2.getRow(row).getCell(1).toString();
                if (chanl_nm.equals(chanl_nm3)) {
                    String usrLevDes = sheetAt2.getRow(row).getCell(3).toString();
                    chanlDetail.setChanlNo(sheetAt2.getRow(row).getCell(0).toString());
                    chanlDetail.setChanlNm(chanl_nm);
                    chanlDetail.setUsrLev(sheetAt2.getRow(row).getCell(2).toString());
                    chanlDetail.setUsrLevDes(usrLevDes);
                    chanlMap.put(usrLevDes, chanlDetail);
                }
                chanlInfoMap.put(chanl_nm3, chanlMap);
            }
        }
    }

    private static void createUsrMgrInfSql(String usr_no, String chanl_no, String usr_nm, String mgr_bran, String bran_mgr_lev, String usr_lev) {
        usrMgrInfSqls_p1.append("insert into USR_MGR_INF(usr_no,chanl_no,usr_nm,mgr_bran,usr_typ,bran_mgr_lev,usr_lev,purv_btmp,opt_usr,opt_date,rec_sts,tran_pswd) " +
                "values ('" + usr_no + "','" + chanl_no + "','" + usr_nm + "','" + mgr_bran + "',null,'" + bran_mgr_lev + "','" + usr_lev + "','',null,null,'0','6FA89230D67A4E8FC60A49D78A2DBB975A675468674F');\r\n");
        usrMgrInfSqls_p8.append("insert into USR_MGR_INF(usr_no,chnl_tpcd,usr_nm,usr_spvs_dept_dsc,usr_tpds,inst_mgt_lvl_dsc,usr_lvl_dsc,usr_ahr_bmp_dsc,add_usr_no,add_dt,usr_rcrd_st,web_bkstg_txn_pswd)  " +
                "values ('" + usr_no + "','" + chanl_no + "','" + usr_nm + "','" + mgr_bran + "','','" + bran_mgr_lev + "','" + usr_lev + "','','330000002','20231115','0','6FA89230D67A4E8FC60A49D78A2DBB975A675468674F');\r\n");
    }

    private static void createUsrInfoSql(String usr_no, String usr_bran, String usr_nm, String sex, String inst_pnp_id) {
        usrInfoSqls_p1.append("insert into USR_INFO(usr_no,nickname,usr_bran,usr_pswd,usr_nm,sex,birday,posn,dept,degr_id,work_id,work_tel,mobil_no,tel_no,home_addr,email_addr,reg_flg,lst_pswd_chg,opt_usr,opt_date,rec_sts,pswd_wrg_tms,inst_pnp_id,ccbins_id)  " +
                "values ('" + usr_no + "','" + usr_nm + "','" + usr_bran + "','6FA89230D67A4E8FC60A49D78A2DBB975A675468674F','" + usr_nm + "','" + sex + "','','','','','','','00000000000','','','','C','','','20231108','0',0,'" + inst_pnp_id + "','" + usr_bran + "');\r\n");
        usrInfoSqls_p8.append("insert into USR_INFO(usr_no,usr_shrtnm,usr_insid,usr_pswd,usr_nm,usr_gnd_dsc,brth_yrmo,post_dsc,dept_dsc,idcard_no,wrk_crtfno,wrk_tel,move_telno,rsdnc_tel,rsdnc_adr,email_adr,land_mod_dsc,pswd_lsttm_mod_dt,add_usr_no,add_dt,usr_rcrd_st,pswd_err_cnt,inst_pnp_id,ccbins_id)  " +
                "values ('" + usr_no + "','','" + usr_bran + "','6FA89230D67A4E8FC60A49D78A2DBB975A675468674F','" + usr_nm + "','" + sex + "','19870829','','','','','010-88888888','18022000707','','','','A','20230915','999990002','20230915','0',0,'" + inst_pnp_id + "','" + usr_bran + "');\r\n");

    }

    private String chanlNo;
    private String chanlNm;
    private String usrLev;
    private String usrLevDes;

    public String getChanlNo() {
        return chanlNo;
    }

    public void setChanlNo(String chanlNo) {
        this.chanlNo = chanlNo;
    }

    public String getChanlNm() {
        return chanlNm;
    }

    public void setChanlNm(String chanlNm) {
        this.chanlNm = chanlNm;
    }

    public String getUsrLev() {
        return usrLev;
    }

    public void setUsrLev(String usrLev) {
        this.usrLev = usrLev;
    }

    public String getUsrLevDes() {
        return usrLevDes;
    }

    public void setUsrLevDes(String usrLevDes) {
        this.usrLevDes = usrLevDes;
    }
}
