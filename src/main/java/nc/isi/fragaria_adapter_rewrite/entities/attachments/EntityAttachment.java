package nc.isi.fragaria_adapter_rewrite.entities.attachments;

import nc.isi.fragaria_adapter_rewrite.entities.Entity;

public interface EntityAttachment extends Entity{
	public String getFileName();
	public void setFileName(String fileName);
	public String getUrl();
	public void setUrl(String url);
	public Entity getParent();
	public void setParent(Entity entity);
	public String getContentType();
	public void setContentType(String contentType);
}
