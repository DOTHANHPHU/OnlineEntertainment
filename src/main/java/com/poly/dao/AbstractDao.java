package com.poly.dao;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.StoredProcedureQuery;
import javax.persistence.TypedQuery;

import com.poly.utils.JpaUtil;

//dùng để gọi các phương thức thêm, xóa... vào 1 cái abstract. Và khi cần dùng mình chỉ cần gọi Abstract ra để dùng và truyền enity cho nó
public class AbstractDao<T> { //"T" là cái ta cần trả về, khi gọi ă phương thức bất kì. VD: User<T>

	public static final EntityManager entityManager = JpaUtil.getEntityManager();

	
	@SuppressWarnings("removal")
//	@SuppressWarnings("deprecation")
	@Override
	protected void finalize() throws Throwable {
		entityManager.close();
		super.finalize();
	}

	public T findById(Class<T> clazz, Integer id) {
		return entityManager.find(clazz, id);
	}

	//findAll các User đang hoạt động nhưng chỉ (WHERE isActive = 1) là chỉ lấy cái User đang hoạt động, còn (isActive = 0 là đã bị xóa thì sẽ k lấy User đó ra)
	public List<T> findAll(Class<T> clazz, boolean exisIsActive) {
		//SELECT o FROM User o WHERE isActive = 1
		//boolean exisIsActive có tồn tại colum là isActive hay k, nếu true thì truyền WHERE và ngược lại
		String entityName = clazz.getSimpleName(); //lấy class Name (Nếu là User nó sẽ lấy cái chữ "User")
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT o FROM ").append(entityName).append(" o");
		if (exisIsActive == true) { //nếu có isActive thì sẽ + thêm WHERE
			sql.append(" WHERE isActive = 1");
		}
		TypedQuery<T> query = entityManager.createQuery(sql.toString(), clazz);
		return query.getResultList();
	}

	//dùng để phân trang
	public List<T> findAll(Class<T> clazz, boolean exisIsActive, int pageNumber, int pageSize) {
		String entityName = clazz.getSimpleName();
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT o FROM ").append(entityName).append(" o");
		if (exisIsActive == true) {
			sql.append(" WHERE isActive = 1");
		}
		TypedQuery<T> query = entityManager.createQuery(sql.toString(), clazz);
		query.setFirstResult((pageNumber - 1) * pageSize);
		query.setMaxResults(pageSize);
		return query.getResultList();

		/*
		 * FirstResult vị trí bắt đầu (sẽ bắt đầu từ 0 nên pageNumber phải -1 để k có page thứ 0) >>> cho phép ng dùng truyền vào từ trang 1 -> ...
		 * MaxResults số lượng thực thể tối ta
		 * pageSize là số lượng phần tử trong 1 page
		 * pageNumber là số trang thứ ...
		 * ----------------------------------------------
		 * 
		 * (pageNumber - 1) * pageSize) có ông thức này vì:
		 * 5 phần tử: [0] [1] [2] [3] [4] 
		 * muốn: 1 trang chứa 2 phần tử (pageSize) >>>> tổng số trang : 3 
		 * 
		 * trang 1: 2 pt >> ([0] [1]) 
		 * trang 2: 2 pt >> ([2] [3]) 
		 * trang 3: 1 pt >> ([4])
		 * 
		 * nhân với pageSize vì: 
		 * 
		 * muốn lấy các pt ở trang thứ 2 >>> pageNumber = 2 >> (pageNumber - 1 >> 2 - 1 = 1), pageSize = 2 
		 * >>> 1 * 2 = 2 >>> bắt đầu lấy từ pt thứ 2, và lấy tổng cộng 2 pt
		 * 
		 * muốn lấy các pt ở trang thứ 3 >>> pageNumber = 3 >> (pageNumber - 1 >> 3 - 1 = 2), pageSize = 2 
		 * >>> 2 * 2 = 4 >>> bắt đầu lấy từ pt thứ 4, và lấy tổng cộng 2 pt
		 */

		// select o from user o where o.username = ?0 and o.password = ?1;
		//2 phương thức findAll tên giống nhau nhưng khác parameter >> dùng phương thức nạp chồng
	}

	//tìm 1 cái gì đó chỉ trả về 1 giá trị
	//tìm theo các column name VD: user sẽ tìm từ "email" or "username", vì k phải lúc nào cũng tìm từ "id"
	public T findOne(Class<T> clazz, String sql, Object... params) {
		// Object... params là tham số có độ dài biến đổi (vì ngta có thể query nhiều điều kiện where)
		//SELECT o FROM User o WHERE o.username = ?0 AND o.password = ?1; 
		//?0 là thứ 0
		// câu sql trên sẽ do enity thiết kế, còn abstract chỉ nhận câu query và set các parameters ở ?0, ?1 đi query
		TypedQuery<T> query = entityManager.createQuery(sql, clazz);

		// findOne(User.class, sql, "phudt", "111")
		//khi gọi findOne thì phải truyền param theo đúng thứ tự ở trên
		for (int i = 0; i < params.length; i++) {// để biết có tổng cộng bao hniêu param
			query.setParameter(i, params[i]);
		}
		
		//trả về List để tránh trường hợp null --> findOne null --> data k trả về phẩn tử nào
		List<T> result = query.getResultList(); //getResultList lấy ra 1 List kết quả
		if (result.isEmpty()) {
			return null;
		}
		return result.get(0);// nếu k empty --> trả về thk thứ 0
		
		
	}

	
	public List<T> findMany(Class<T> clazz, String sql, Object... params) {
		TypedQuery<T> query = entityManager.createQuery(sql, clazz);
		for (int i = 0; i < params.length; i++) {
			query.setParameter(i, params[i]);
		}
		return query.getResultList();
	}

	//NativeQuery sẽ không truyền class, ko trả về List<T>, nó chỉ gom giá trí của các bảng khác nhau về 1 Object >> trả về 1 mảng Object
	@SuppressWarnings("unchecked")
	public List<Object[]> findManyByNativeQuery(String sql, Object... params) {
		Query query = entityManager.createNativeQuery(sql);
		
		for (int i = 0; i < params.length; i++) {
			query.setParameter(i, params[i]);
		}
		return query.getResultList();
	}
	
	
	public T create(T entity) {
		try {
			entityManager.getTransaction().begin(); // Bắt đầu Transaction
			entityManager.persist(entity);
			entityManager.getTransaction().commit(); // Chấp nhận kết quả thao tác	
			System.out.println("Create success! ");
			return entity;
		} catch (Exception e) {
			entityManager.getTransaction().rollback(); // Hủy thao tác nếu lỗi		
			System.out.println("Cannot insert entity !" + entity.getClass().getSimpleName() + "to DB");
			throw new RuntimeException(e); //ném lỗi
		}
	}
	
	public T update(T entity) {
		try {
			entityManager.getTransaction().begin();
			entityManager.merge(entity);
			entityManager.getTransaction().commit();	
			System.out.println("Update success! ");
			return entity;
		} catch (Exception e) {
			entityManager.getTransaction().rollback();		
			System.out.println("Cannot update entity !" + entity.getClass().getSimpleName() + "to DB");
			throw new RuntimeException(e);
		}
	}
	
	public T delete(T entity) {
		try {
			entityManager.getTransaction().begin();
			entityManager.remove(entity);
			entityManager.getTransaction().commit();	
			System.out.println("Delete success! ");
			return entity;
		} catch (Exception e) {
			entityManager.getTransaction().rollback();		
			System.out.println("Cannot delete entity !" + entity.getClass().getSimpleName() + "to DB");
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<T> callStored(String namedStored, Map<String, Object> params) {
		StoredProcedureQuery query = entityManager.createNamedStoredProcedureQuery(namedStored);
		params.forEach((key, value) -> query.setParameter(key, value));
		return (List<T>) query.getResultList();
		
	}

}
