package xupt.se.ttms.dao;

import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.List;

import xupt.se.ttms.idao.iPlayDAO;
import xupt.se.ttms.model.Play;
import xupt.se.util.DBUtil;

public class PlayDAO implements iPlayDAO
{
    @SuppressWarnings("finally")
    @Override
    public int insert(Play ply)
    {
        int result=0;
        DBUtil db=new DBUtil();
        db.openConnection();
        int dict_type_id=0;
        int dict_lang_id=0;
        //System.out.println("已经进入PlayDAO");
        try
        {
        	String sql2="select dict_id from data_dict where dict_value like '%" + ply.getDictTypeId() + "%' ";
            String sql3="select dict_id from data_dict where dict_value like '%" + ply.getDictLangId() + "%' ";
            String PlayStatus="";
            System.out.println("sql2:"+sql2);
            System.out.println("sql3:"+sql3);
        	try {
            	ResultSet resultSet=db.execQuery(sql2);
            	while(resultSet.next())
            	{
            		 dict_type_id=resultSet.getInt("dict_id");
            	}
            }catch(Exception e) {
            	throw new RuntimeException("查找信息失败",e);
            }
        	try {
            	ResultSet resultSet1=db.execQuery(sql3);
            	while(resultSet1.next())
            	{
            		 dict_lang_id=resultSet1.getInt("dict_id");
            	}
            }catch(Exception e) {
            	throw new RuntimeException("查找信息失败",e);
            }
        	System.out.println(ply.getPlayStatus());
        	if(ply.getPlayStatus().equals("已安排"))
        	{
        		System.out.println("wozai");
        		PlayStatus="1";
        	}
        	else if(ply.getPlayStatus().equals("待安排"))
        	{
        		PlayStatus="0";
        	}
        	else if(ply.getPlayStatus().equals("已下线"))
        	{
        		PlayStatus="-1";
        	}
        	System.out.println(PlayStatus);
            //以上为转换过程！
            String sql="insert into play( dict_type_id, dict_lang_id, play_name,play_introduction,play_image,play_length,play_ticket_price,play_status )"
                    + "values ("+dict_type_id+", "+dict_lang_id+", '"+ply.getPlayName()+"', '"+
            		ply.getPlayIntroduction()+"', '"+ply.getPlayImage()+"',"+ply.getPlayLength()+", "+ply.getPlayTicketPrice()+", "+PlayStatus+")";
            System.out.println(sql);//!!!!!!!!!
            ResultSet rst=db.getInsertObjectIDs(sql);
            if(rst != null && rst.first())
            {
                ply.setPlayId(rst.getInt(1));
            }
            db.close(rst);
            db.close();
            result=1;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            return result;
        }
    }

    @SuppressWarnings("finally")
    @Override
    public int update(Play ply)
    {
        int result=0;
        DBUtil db=new DBUtil();
        db.openConnection(); 
        try
        {
        	String sql2="select dict_id from data_dict where dict_value like '%" + ply.getDictTypeId() + "%' ";
            String sql3="select dict_id from data_dict where dict_value like '%" + ply.getDictLangId() + "%' ";
            String PlayStatus="";
            int dict_type_id=0;
            int dict_lang_id=0;
        	try {
            	ResultSet resultSet=db.execQuery(sql2);
            	ResultSet resultSet1=db.execQuery(sql3);
            	while(resultSet.next())
            	{
            		 dict_type_id=resultSet.getInt("dict_id");

            	}
            	while(resultSet1.next())
            	{
           		     dict_lang_id=resultSet1.getInt("dict_id");
            	}

            }catch(Exception e) {
            	throw new RuntimeException("查找信息失败",e);
            }
        	if(ply.getPlayStatus().equals("已安排"))
        	{
        		PlayStatus="1";
        	}
        	else if(ply.getPlayStatus().equals("待安排"))
        	{
        		PlayStatus="0";
        	}
        	else if(ply.getPlayStatus().equals("已下线"))
        	{
        		PlayStatus="-1";
        	}
            String sql="update play set " +" dict_type_id = "+dict_type_id+","+"dict_lang_id ="+dict_lang_id +",play_name ='" + ply.getPlayName() + "', " + 
            		" play_introduction = '"+ ply.getPlayIntroduction() + "', " +"play_image = '"+ply.getPlayImage()+ "', play_length = " + ply.getPlayLength() + ", "
                    + " play_ticket_price = '" + ply.getPlayTicketPrice() + "', "+"play_status = '"+PlayStatus+"'";
            sql+=" where play_id = " + ply.getPlayId();
           // System.out.println("sql:"+sql);
            result=db.execCommand(sql);
            db.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            return result;
        }
    }

    @Override
    public int delete(int PlayID)
    {
        int result=0;
        try
        {
            DBUtil db=new DBUtil(); 
            db.openConnection(); 
            String sql="delete from  play where play_id = " + PlayID;
            String sql2="select sched_id,sched_status from schedule where play_id = " + PlayID;
            //System.out.println("11:"+PlayID);
            int SchedId=0;
            String status="";
            try {
            	ResultSet resultSet=null;
            	resultSet=db.execQuery(sql2);
            	if(resultSet!=null) {
            	while(resultSet.next())
            	{
            		 SchedId=resultSet.getInt("sched_id");
            		 status=resultSet.getString("sched_status");
            		 if(status.equals("0"))
                     {
            	          String sql3="delete from schedule where play_id = " + PlayID;
            	          String sql4="delete from ticket where sched_id = " + SchedId;
            	          db.execCommand(sql4);
                          db.execCommand(sql3);
                      }
            	}
            	}
            }catch(Exception e) {
            	throw new RuntimeException("查找信息失败",e);
            }
            db.openConnection();
            result=db.execCommand(sql);
            db.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return result;
    }

    @SuppressWarnings("finally")
    public String selectplayid(int condt)
    {
        DBUtil db=null;
        String result="";
        try
        {
            String sql="select play_name from play  where play_id= " + condt;
            db=new DBUtil();
            if(!db.openConnection())
            {
                System.out.print("fail to connect database");
                return null;
            }
            ResultSet rst=db.execQuery(sql);
            if(rst != null)
            {
                while(rst.next())
                {
                    result=rst.getString("play_name");
                }
            }
            db.close(rst);
            db.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            return result;
        }
    }

    @SuppressWarnings("finally")
    @Override
    public List<Play> select(String PlayName)
    {
        DBUtil db=null;
        List<Play> plyList=null;
        plyList=new LinkedList<Play>();
        String dict_type_id="";
        String dict_lang_id="";
        try
        {
            PlayName.trim();
            String sql1="select * from play where play_name like '%" + PlayName + "%'";
            db=new DBUtil();
            if(!db.openConnection())
            {
                System.out.print("fail to connect database");
                return null;
            }
            ResultSet rst=db.execQuery(sql1);
            if(rst != null)
            {
                while(rst.next())
                {
                    Play ply=new Play();
                	String sql2="select dict_value from data_dict where dict_id like " +rst.getString("dict_type_id") ;
                    String sql3="select dict_value from data_dict where dict_id like " +rst.getString("dict_lang_id") ;
                    String PlayStatus="";
                	try {
                    	ResultSet resultSet=db.execQuery(sql2);
                    	ResultSet resultSet1=db.execQuery(sql3);
                    	while(resultSet.next())
                    	{
                    		 dict_type_id=resultSet.getString("dict_value");

                    	}
                    	while(resultSet1.next())
                    	{
                   		     dict_lang_id=resultSet1.getString("dict_value");
                    	}
                    }catch(Exception e) {
                    	throw new RuntimeException("查找信息失败",e);
                    }
                	if(rst.getString("play_status").equals("1"))
                	{
                		PlayStatus="已安排";
                	}
                	else if(rst.getString("play_status").equals("0"))
                	{
                		PlayStatus="待安排";
                	}
                	else if(rst.getString("play_status").equals("-1"))
                	{
                		PlayStatus="已下线";
                	}

                    ply.setPlayId(rst.getInt("play_id"));
                    ply.setDictTypeId(dict_type_id);
                    ply.setDictLangId(dict_lang_id);
                    ply.setPlayName(rst.getString("play_name"));
                    ply.setPlayIntroduction(rst.getString("play_introduction"));
                    ply.setPlayImage(rst.getString("play_image"));
                    //System.out.println("imag:"+rst.getString("play_image"));
                    ply.setPlayLength(rst.getInt("play_length"));
                    ply.setPlayTicketPrice(rst.getString("play_ticket_price"));
                    ply.setPlayStatus(PlayStatus);
                    plyList.add(ply);
                }
            }
            db.close(rst);
            db.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            return plyList;
        }
    }

}