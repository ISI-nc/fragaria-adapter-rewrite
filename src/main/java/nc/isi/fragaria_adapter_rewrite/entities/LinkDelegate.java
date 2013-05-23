package nc.isi.fragaria_adapter_rewrite.entities;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;

import nc.isi.fragaria_adapter_rewrite.dao.ByViewQuery;
import nc.isi.fragaria_adapter_rewrite.entities.Link.Side;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

public class LinkDelegate<T extends Link<?, ?>, O extends Entity> {

	private final String propName;
	private final Class<T> type;
	private final Entity entity;
	private final Class<O> othersType;

	public LinkDelegate(String propName, Class<T> type, Entity entity,
			Class<O> othersType) {
		this.propName = propName;
		this.type = type;
		this.entity = entity;
		this.othersType = othersType;
	}

	public void set(Collection<T> links) {
		for (T link : get()) {
			if (links.contains(link)) {
				continue;
			}
			entity.getSession().delete(link);
		}
		entity.writeProperty(propName, links);
	}

	public Collection<O> getOthers() {
		Collection<String> ids = Sets.newHashSet();
		Side side = null;
		for (T link : get()) {
			if (side == null) {
				side = Side.opposite(getThisSide(link));
			}
			System.out.println(link.getId());
			ids.add(link.get(side).getId());
		}
		return entity.getSession().get(
				new ByViewQuery<>(othersType, null).filterBy(Entity.ID, ids));
	}
	
	public Collection<O> getOthers(Collection<T> links) {
		Collection<String> ids = Sets.newHashSet();
		Side side = null;
		for (T link : links) {
			if (side == null) {
				side = Side.opposite(getThisSide(link));
			}
			ids.add(link.get(side).getId());
		}
		return entity.getSession().get(
				new ByViewQuery<>(othersType, null).filterBy(Entity.ID, ids));
	}

	public void setOthers(Collection<O> others) {
		set(prepareOthers(others));
	}
	
	public Collection<T> prepareOthers(Collection<O> others) {
		Collection<T> links = Sets.newHashSet();
		for (O entity : others) {
			T link = contains(entity);
			links.add(link == null ? buildLink(entity) : link);
		}
		return links;
	}
	
	public T prepareOther(O other) {
		T link = contains(other);
		return link == null ? buildLink(other) : link;
	}
	public Boolean add(O entity) {
		checkNotNull(entity);
		return add(buildLink(entity));
	}

	protected T buildLink(O entity) {
		T link = this.entity.getSession().create(type);
		Side side = getThisSide(link);
		link.set(side, this.entity);
		link.set(Side.opposite(side), entity);
		return link;
	}

	public Boolean remove(O entity) {
		checkNotNull(entity);
		T link = contains(entity);
		return link != null ? remove(link) : false;
	}

	public T contains(O entity) {
		Side side = null;
		for (T link : get()) {
			if (side == null) {
				side = Side.opposite(getThisSide(link));
			}
			if (Objects.equal(link.get(side), entity)) {
				return link;
			}
		}
		return null;
	}

	private Side getThisSide(Link<?, ?> link) {
		return link.isR(entity) ? Side.R : Side.L;
	}

	public Collection<T> get() {
		return ImmutableSet.copyOf(entity.readCollection(type, propName));
	}
	
	public Boolean add(T link) {
		checkArgument(!get().contains(link), "link already exists");
		return entity.add(propName, link, type);
	}

	public Boolean remove(T link) {
		boolean result = entity.remove(propName, link, type);
		if (result) {
			entity.getSession().delete(link);
		}
		return result;
	}

}
