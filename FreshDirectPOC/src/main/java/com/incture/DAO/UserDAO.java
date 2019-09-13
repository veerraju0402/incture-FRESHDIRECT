package com.incture.DAO;


import java.util.ArrayList;
import java.util.List;




import org.hibernate.Query;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.incture.DTO.UserDTO;
import com.incture.entity.UserDO;
import com.incture.response.ResponseMessage;
import com.incture.utility.SendMailTLS;
import com.incture.utility.StringUtils;

@Repository
public class UserDAO extends BaseDAO {

	@Autowired
	SendMailTLS sendMailTLS;


	// import
	public UserDO importToDB(UserDTO userDTO) {
		UserDO userDO = new UserDO();
		BeanUtils.copyProperties(userDTO, userDO);
		
		return userDO;
	}


	// export
	public UserDTO ExportDB1(UserDO userDO) {
		UserDTO userDTO = new UserDTO();
		BeanUtils.copyProperties(userDO, userDTO);
	

		return userDTO;
	}
	// -------------------------------------------------

	//autogenerated id
	public String getId() {
		session = getSession();
		Query q = session.createQuery("SELECT MAX(userId) from UserDO");
		String e = (String) q.uniqueResult();
		//if(e != null | !e.equals(null)){
		if (!StringUtils.isEmpty(e)) {
			e = e.substring(4, 8);
			Integer i = Integer.valueOf(e);
			String ss=String.format("%04d%n",++i);
			String s="INCT"+ss;
			s=s.trim();
			s=s.substring(0,8);
			return s;
		}
		else 
			return e="INCT0001";
	}


	// adding user details
	public ResponseMessage addUserDO(UserDTO userDTO) {
		//UserResponse response=new UserResponse();
		ResponseMessage response=new ResponseMessage();
		String mailMessage=null;
		if(StringUtils.isEmpty(userDTO.getEmail()) || StringUtils.isEmpty(userDTO.getVendorName()) ){
			response.setMessage("Mail or vendor name cannot be null");
			response.setStatusCode(200);
			return response;
		}
		try {  

			session=getSession();
			UserDO userDO=importToDB(userDTO);
			String uniqueId=userDO.getUserId();
			System.err.println("id is "+uniqueId);
			mailMessage=sendMailTLS.main(userDTO.getEmail(),uniqueId);
			if(mailMessage.equalsIgnoreCase("Mail sent")){
				session.save(userDO);
				response.setMessage("User saved to DB");
			}else{
				response.setMessage("failed to Save Data, "+mailMessage); 
			}
			response.setStatusCode(200);
			return response;  
		} catch (Exception e) {
			System.err.println("exception:-"+e.getMessage());
			response.setMessage("Exception - "+e.getMessage());
			response.setStatusCode(500);
			return response;
		}

	}


	public ResponseMessage getAllUserDO() {
		//UserResponse response=new UserResponse();
		ResponseMessage response=new ResponseMessage();
		//List<UserDTO> userDTOList=new ArrayList<UserDTO>();
		List<Object> userDTOList=new ArrayList<Object>();
		try{
			session=getSession();
			List<UserDO> userDOList=session.createQuery("from UserDO").list();
			if(userDOList.size()>0 ){
				for(UserDO userDO1:userDOList){
					UserDTO userDTO=ExportDB1(userDO1);
					userDTOList.add(userDTO);
				}
				response.setStatusCode(200);
				response.setMessage("success");
				//response.setUserDTOList(userDTOList);
				response.setObjList(userDTOList);
				return response;
			}else {
				response.setStatusCode(200);
				response.setMessage("No records Exists");
				return response;
			}	
		}	catch(Exception e){
			System.err.println("exception:-"+e.getMessage());
			response.setStatusCode(500);
			response.setMessage("Exception - "+e.getMessage());
			return response;
		}

	}

	//get user by id
	public ResponseMessage getUserById(String userId) {
		//UserResponse response=new UserResponse();
		ResponseMessage response=new ResponseMessage();
		try{
			session=getSession();
			UserDO userDO = session.get(UserDO.class,userId); 
			if(!StringUtils.isEmptyObject(userDO)){
				/*
			 Query query = session.createQuery("from UserDO where userId =:Id");
			query.setParameter("Id", userId);
			UserDO userDO=(UserDO) query.uniqueResult();*/
				/*
			UserDO userDO = (UserDO) session.createQuery("from UserDO where userId=" +"'"+ userId+"'").uniqueResult();
				 */
				UserDTO userDTO=ExportDB1(userDO);
				response.setStatusCode(200);
				response.setMessage("success");
				//response.setUserDTO(userDTO);
				response.setObj(userDTO);
				return response;	
			}else{
				response.setStatusCode(200);
				response.setMessage("No record exists");
				return response;	
			}
		}	catch(Exception e){
			e.printStackTrace();
			System.err.println("exception:- "+e.getMessage());
			response.setStatusCode(500);
			response.setMessage("Exception - "+e.getMessage());
			return response;
		}
	}


	public ResponseMessage getUserByVendor(String vendorName) {
		//UserResponse response=new UserResponse();
		ResponseMessage response=new ResponseMessage();
		try{
			session=getSession();
			UserDO userDO = (UserDO) session.createQuery("from UserDO where vendorName=" +"'"+ vendorName+"'").uniqueResult();
			if(!StringUtils.isEmptyObject(userDO)){
				UserDTO userDTO=ExportDB1(userDO);
				response.setStatusCode(200);
				response.setMessage("success");
				//response.setUserDTO(userDTO);
				response.setObj(userDTO);
				return response;}
			else{
				response.setStatusCode(200);
				response.setMessage("No record Exists");
				return response;	
			}
		}	catch(Exception e){
			System.err.print("Exception:- "+e.getMessage());
			response.setStatusCode(500);
			response.setMessage("Exception - "+e.getMessage());
			return response;
		}
	}


	public ResponseMessage getUserColumn(String userId) {
		//UserResponse response=new UserResponse();
		ResponseMessage response=new ResponseMessage();
		try{
			session=getSession();
			Object obj=session.createQuery("select u.vendorName from UserDO u where u.userId=" +"'"+ userId+"'").uniqueResult();
			if(!StringUtils.isEmptyObject(obj)){
				String vendorName=(String) obj;
				response.setStatusCode(200);
				response.setMessage(vendorName);
				return response;
			}else {
				response.setStatusCode(200);
				response.setMessage("No record Exists");
				return response;
			}
		}	catch(Exception e){
			System.err.println("Exception:- "+e.getMessage());
			response.setStatusCode(500);
			response.setMessage("Exception - "+e.getMessage());
			return response;
		}
	}

	public ResponseMessage deleteUserDO(String userId) {
		//UserResponse response=new UserResponse();
		ResponseMessage response=new ResponseMessage();
		try {
			session = getSession();
			int count=session.createQuery("delete UserDO where userId=" +"'"+ userId+"'").executeUpdate();
			if(count==0){
				response.setStatusCode(200);
				response.setMessage("No record Exists");
				return response;}
			else{
				response.setStatusCode(200);
				response.setMessage("DELETED");
				return response;
			}
		} catch (Exception e) {
			System.err.println("Exception:- " + e.getMessage());
			response.setStatusCode(500);
			response.setMessage("failed to delete:-"+e.getMessage());
			return response;
		}
	}

	//update method
	public ResponseMessage updateUserDO(UserDTO userDTO) {
		//UserResponse response=new UserResponse();
		ResponseMessage response=new ResponseMessage();
		if(!StringUtils.isEmpty(userDTO.getUserId())){
			System.err.println("user id not mentioned");
			response.setStatusCode(200);
			response.setMessage("user id not mentioned");
			return response;
		} 
		try{
			session =getSession();
			UserDO old = session.get(UserDO.class,userDTO.getUserId());
			UserDO newUser=importToDB(userDTO);

			if(!StringUtils.isEmpty(newUser.getEmail()) ){
				old.setEmail(newUser.getEmail());
			}
			if(!StringUtils.isEmpty(newUser.getFirstName())){
				old.setFirstName(newUser.getFirstName());
			}
			if(!StringUtils.isEmpty(newUser.getLastName()) ){
				old.setLastName(newUser.getLastName());
			}
			if(!StringUtils.isEmpty(newUser.getPanNumber()) ){
				old.setPanNumber(newUser.getPanNumber());
			}
			if(!StringUtils.isEmpty(newUser.getPhoneNumber()) ){
				old.setPhoneNumber(newUser.getPhoneNumber());
			}
			if(!StringUtils.isEmpty(newUser.getServiceType()) ){
				old.setServiceType(newUser.getServiceType());
			}

			if(!StringUtils.isEmpty(newUser.getUserRole()) ){
				old.setUserRole(newUser.getUserRole());
			}
			if(!StringUtils.isEmpty(newUser.getVendorAddress())  ){
				old.setVendorAddress(newUser.getVendorAddress());
			}
			if(!StringUtils.isEmpty(newUser.getVendorName()) ){
				old.setVendorName(newUser.getVendorName());
			}

			session.update(old);
			response.setStatusCode(200);
			response.setMessage("updated");
			return response;
		}catch(Exception e){
			System.err.println("Exception:-"+e.getMessage());
			response.setStatusCode(500);
			response.setMessage("failed to update:-"+e.getMessage());
			return response;
		}
	}


}
