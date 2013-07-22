package nc.isi.fragaria_adapter_rewrite.entities.attachments;

import nc.isi.fragaria_adapter_rewrite.entities.AbstractEntity;
import nc.isi.fragaria_adapter_rewrite.entities.Entity;

public interface EntityAttachment<T extends AbstractEntity> extends Entity{
	public String getFileName();
	public void setFileName(String fileName);
	public String getUrl();
	public void setUrl(String url);
	public T getParent();
	public void setParent(T entity);
	public String getContentType();
	public void setContentType(String contentType);
}
