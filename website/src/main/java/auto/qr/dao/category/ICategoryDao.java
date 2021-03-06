package auto.qr.dao.category;

import java.util.List;

import auto.datamodel.dao.Category;

public interface ICategoryDao {

	List<Category> getCategory(Category category);

	Category getCategoryByCnameAndLev(String automobile, int ordinal);

}
