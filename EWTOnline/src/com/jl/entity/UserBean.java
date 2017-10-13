package com.jl.entity;

public class UserBean
{
   private String s1;
   private String s2;
   public UserBean()
   {
	   s1="";
	   s2="";
   }
   public UserBean(String cs)
   {
	   s1 = cs;
   }
   public void Intial(String cs)
   {
	   s1 = cs;
   }
   public boolean append1(String sItem,String sVal,int mode)
   {
	   if(sVal==null || sVal.isEmpty()) return false;
	   if(mode != 1)
	   {
		   sVal=sVal.trim().replaceAll("'","");
		   if(sVal.isEmpty()) return false;
	   }
	   if(!s1.isEmpty()) s1+=" and ";
	   if(mode==1)		s1+=sItem+"="+sVal;
	   else if(mode==2)	s1+=sItem+"='"+sVal+"'";
	   else if(mode==3)	s1+=sItem+" like '%"+sVal+"%'";
	   return true;
   }
   
   public void append1(String s)
   {
	   if(!s1.isEmpty()) s1+=" and ";
	   s1 += s;
   }
   
   public void append2(String sItem,String sVal,boolean bint,boolean bnew)
   {
	   sVal=sVal.trim().replaceAll("'", "");
	   if(!s1.isEmpty()) s1+=",";
	   if(bnew)
	   {
		   if(!s2.isEmpty()) s2+=",";
		   s1+=sItem;
		   if(bint)	s2+=sVal;
		   else s2+="'"+sVal+"'";
	   }
	   else
	   {
		   if(bint)	s1+=sItem+"="+sVal;
		   else s1+=sItem+"='"+sVal+"'";
	   }	   
   }
   
   public boolean append3(String s)
   {
	   if(s.isEmpty()) return false;
	   if(!s1.isEmpty()) s1+=",";
	   s1 += s;
	   return true;
   }
   
   public void append4(String s)
   {
	   if(!s1.isEmpty()) s1+=",";
	   s1 += s;
   }
  
   public String result()
   {
	   return this.s1;
   }
   
   public String result1()
   {
	   return this.s1;
   }
   public String result2()
   {
	   return this.s2;
   }
   
   public boolean isEmpty()
   {
	   return this.s1.isEmpty();
   }
   
   public void Empty()
   {
	   s1="";
	   s2="";
   }
}

