package auto.datamodel.dao;

import static javax.persistence.GenerationType.IDENTITY;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.NoArgsConstructor;
import auto.datamodel.cache.ICacheable;
import auto.util.SerializeUtils;

/**
 * @author wangWentao
 *
 */
@NoArgsConstructor
@Entity
@Table(name = "brand")
public class Brand implements java.io.Serializable, ICacheable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8895238321542130398L;
	private static final Brand EMPTY = new Brand(0L);
	
	private Long id;
	
	/**
	 * 品牌首字母
	 */
	private String brandKey;
	
	/**
	 * 品牌logoUrl
	 */
	private String brandLogoUrl;
	
	/**
	 * 品牌名称
	 */
	private String brandName;
	
	/**
	 * 品牌状态 0：正常使用 1：已停用
	 */
	private Integer brandStatus;
	
	/**
	 * 创建时间
	 */
	private Date creatTime;
	
	/**
	 * 修改时间
	 */
	private Date modifiedTime;

	public Brand(Long id) {
		this.id = id;
	}

	@Id
	@GeneratedValue(strategy = IDENTITY)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getBrandKey() {
		return brandKey;
	}

	public void setBrandKey(String brandKey) {
		this.brandKey = brandKey;
	}

	public String getBrandLogoUrl() {
		return brandLogoUrl;
	}

	public void setBrandLogoUrl(String brandLogoUrl) {
		this.brandLogoUrl = brandLogoUrl;
	}

	public String getBrandName() {
		return brandName;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}

	public int getBrandStatus() {
		return brandStatus;
	}

	public void setBrandStatus(Integer brandStatus) {
		this.brandStatus = brandStatus;
	}

	public Date getCreatTime() {
		return creatTime;
	}

	public void setCreatTime(Date creatTime) {
		this.creatTime = creatTime;
	}

	public Date getModifiedTime() {
		return modifiedTime;
	}

	public void setModifiedTime(Date modifiedTime) {
		this.modifiedTime = modifiedTime;
	}

	@Override
	public void writeFields(DataOutput out) throws IOException {
		// TODO Auto-generated method stub
		out.writeLong(id);
		SerializeUtils.writeString(out, brandKey);
		SerializeUtils.writeString(out, brandLogoUrl);
		SerializeUtils.writeString(out, brandName);
		SerializeUtils.writeInt(out, brandStatus);
		SerializeUtils.writeDate(out, creatTime);
		SerializeUtils.writeDate(out, modifiedTime);
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		// TODO Auto-generated method stub
		id = in.readLong();
		brandKey = SerializeUtils.readString(in);
		brandLogoUrl = SerializeUtils.readString(in);
		brandName = SerializeUtils.readString(in);
		brandStatus = SerializeUtils.readInt(in);
		creatTime = SerializeUtils.readDate(in);
		modifiedTime = SerializeUtils.readDate(in);
	}

	@Transient
	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return EMPTY.getId().equals(id);
	}
}
