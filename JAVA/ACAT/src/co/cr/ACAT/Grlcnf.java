/*
 * Copyright (C) 2019 Rodolfo Navarro B.
 *
 * This program is free software: you can redistribute it and/or modify
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 */
package co.cr.ACAT;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Rodolfo Navarro
 * @version 1.0.0
 */
public class Grlcnf {
    public static enum QueryType {
        update,
        insert,
        delete,
        select,
        begin,
        commit,
        rollback,
        other,
        show
    }
    public static final Pattern FIRST_WORD = Pattern.compile("^\\s*(\\S+)",
           Pattern.CASE_INSENSITIVE);
    
    public QueryType getQueryType(String query) {
        Matcher matcher = FIRST_WORD.matcher(query);
        if (matcher.find()) {
            try {
                return QueryType.valueOf(matcher.group(1).toLowerCase());
            } catch (IllegalArgumentException ignore) {
                throw new RuntimeException(ignore);
            }
        }
        return QueryType.other;
    }
    public Integer Convertboolint(Boolean b){
        return b ? 1 : 0;
    }
    public Boolean Convertintbol(Integer b){
        return b==1;
    }
    
    public String smpDateftransf(String fsql){
        String rtDst="";
        switch (fsql) {
            case "dmy":
                rtDst="dd-MM-yyyy";
                break;
            case "mdy":
                rtDst="MM-dd-yyyy";
                break;
            case "ymd":
                rtDst="yyyy-MM-dd";
                break;
            default:
                rtDst="yyyy-MM-dd";
        }
        return rtDst;
    }
    
    public String DateChangeFormat(String Datetchn,String fsql){
        String dcf="null";
        DateFormat dateymd = new SimpleDateFormat("yyyy-MM-dd");
        Date datex = new Date();
        SimpleDateFormat dfdbsql = new SimpleDateFormat(fsql);
        Datetchn = Datetchn.replaceAll("/", "-");
        int py=Datetchn.indexOf("-");
        if (py==2){
            Datetchn =Datetchn.substring(6,Datetchn.length()) + Datetchn.substring(py,py+3) + "-" + Datetchn.substring(0,py);
        }
          
        try {
            Date dates = new Date();
            dates = dateymd.parse(Datetchn);
            dcf=dfdbsql.format(dates);
        } catch (ParseException ex) {
        }
        return dcf;
    }
    
    public String GetValType(String valor,String ctip,String fmtdatesql) throws ParseException{
        SimpleDateFormat formatter;
        Date md=new Date();
        switch (ctip){
            case "int":
            case "numeric":
            case "decimal":
            case "smallint":
            case "real":
            case "smallmoney":
            case "tinyint":
            case "float":
                valor=valor + ",";
                break;
            case "datetime":
                formatter = new SimpleDateFormat(fmtdatesql+" HH:mm:ss");
                md=formatter.parse(valor);
                valor=md.toString();
                break;
            case "date":
                formatter = new SimpleDateFormat(fmtdatesql);
                md=formatter.parse(valor);
                valor=md.toString();
                break;
            case "time":
            case "smalldatetime":
            case "timestamp":
                valor= "'"+valor + "',";
                break;
            case "nvarchar":
            case "nchar":
            case "ntext":
            case "text":
            case "varchar":
                valor= "'"+valor + "',";
                break;
        }
        return valor;
    }
}
