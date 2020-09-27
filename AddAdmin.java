package cgd.action;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import org.apache.struts2.interceptor.SessionAware;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import com.opensymphony.xwork2.ActionSupport;
import cgd.bean.Admin;
import cgd.bean.AllPlace;
import cgd.bean.Place;
import cgd.util.HibernateUtil;

public class AddAdmin extends ActionSupport implements SessionAware{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Map<String, Object> session;
	private Admin admin;
	private String confirmPassword;
	private Session session_h = null;
	Transaction tx = null;
	private List list = null;
	
	
	public String add_admin(){
		
		session_h = HibernateUtil.getSession();
		tx=session_h.beginTransaction();
		admin.setImage("../upload/xiao.jpg");
		Place p=null;
		if(admin.getPermission().equals("一级管理员")){
			
			p = new Place();
			p.setPlace(admin.getPlace());
			System.out.println(admin.getPlace());
			
			
			ArrayList<Place> ps = (ArrayList<Place>) session.get("places");
			ArrayList<AllPlace> aps = (ArrayList<AllPlace>) session.get("allPlaces");
			ps.add(p);
			session.put("places", ps);
			session.put("places_count", ps.size());
			
			
			admin.setPid((Integer)session.get("admin_id"));
			
	   		//修改总地区的
	   		for(int i=0;i<aps.size();i++){
	   			AllPlace ap = aps.get(i);
	   			if(ap.getPlace().equals(admin.getPlace())){
	   				aps.remove(i);
	   				AllPlace apt = session_h.get(AllPlace.class, ap.getId());
	   				apt.setFlag("1");
	   				session_h.update(apt);
	   				session.put("allPlaces", aps);
	   				session.put("allPlaces_count", aps.size());
	   				break;
	   			}
	   		}
	   				
	   		
		}else{
			
			admin.setPid(0);
		}
		try{
			session_h.save(admin);
			session_h.save(p);
			tx.commit();
			System.out.println("add_admin success");
			this.addActionMessage("添加成功");
			return "admin_add";
		}catch(HibernateException e){
			if (tx!=null) tx.rollback();
	         e.printStackTrace();
		}finally {
	    	  session_h.close(); 
	    }
		this.addActionMessage("添加失败！用户名重复！");
		return "admin_add_failure";
		
	}
	public String update_admin(){
		System.out.println(confirmPassword);
		System.out.println(admin.getPassword());
		if(this.admin.getPassword().equals(this.confirmPassword)){
			System.out.println("come in");
			session_h = HibernateUtil.getSession();
			tx=session_h.beginTransaction();

			Admin ad = (Admin)session_h.get(Admin.class, (int)session.get("admin_id"));
			ad.setName(this.admin.getName());
			ad.setPassword(this.admin.getPassword());
			ad.setAge(this.admin.getAge());
			ad.setPhone(this.admin.getPhone());
			
			
			session_h.update(ad);
			tx.commit();
			session_h.close();
			
			session.put("admin_name", this.admin.getName());
			session.put("admin_password", this.admin.getPassword());
			session.put("admin_age", this.admin.getAge());
			session.put("admin_phone", this.admin.getPhone());
			System.out.println("admin_update");
			this.addActionMessage("修改成功");
			return "admin_update";
		}else
			this.addActionMessage("修改失败");
			return "admin_update_failure";
		
		
	}
	
	
	
	@Override
	public void setSession(Map<String, Object> arg0) {
		// TODO Auto-generated method stub
		this.session = arg0;
	}
	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}
	public Admin getAdmin() {
		return admin;
	}

	public void setAdmin(Admin admin) {
		this.admin = admin;
	}
}
