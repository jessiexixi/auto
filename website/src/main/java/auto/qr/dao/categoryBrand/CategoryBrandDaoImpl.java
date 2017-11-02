package auto.qr.dao.categoryBrand;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import auto.dao.impl.ReadonlyDaoImpl;
import auto.datamodel.BrandStatus;
import auto.datamodel.dao.CategoryBrand;

@Repository
public class CategoryBrandDaoImpl extends ReadonlyDaoImpl implements ICategoryBrandDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<CategoryBrand> getCategoryBrandBySecondLevId(Long id) {
		Criteria criteria = getSession().createCriteria(CategoryBrand.class);
		criteria.add(Restrictions.eq("secondLevId", id))
				.add(Restrictions.eq("brandStatus", BrandStatus.NORMAL.ordinal()));
		return criteria.list();
	}

}
