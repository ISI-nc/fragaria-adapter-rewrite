package nc.isi.fragaria_adapter_rewrite.entities;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.mysema.query.alias.Alias.$;
import static com.mysema.query.alias.Alias.alias;
import static com.mysema.query.collections.MiniApi.from;

import java.util.Collection;

import nc.isi.fragaria_adapter_rewrite.entities.Link.Side;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.mysema.query.BooleanBuilder;

public class LinkDelegate<L extends Entity, R extends Entity, T extends Link<L, R>> {

	private final String propName;
	private final Class<T> type;
	private final Entity entity;

	public LinkDelegate(String propName, Class<T> type, Entity entity) {
		this.propName = propName;
		this.type = type;
		this.entity = entity;
	}

	public void set(Collection<T> links) {
		for (T link : get()) {
			remove(link);
		}
		entity.writeProperty(propName, links);
	}

	public Collection<Entity> getOthers() {
		Collection<Entity> others = Sets.newHashSet();
		for (T link : get()) {
			Side side = Side.opposite(getThisSide(link));
			others.add(link.get(side));
		}
		return ImmutableSet.copyOf(others);
	}

	public void setOthers(Collection<? extends Entity> others) {
		Collection<T> links = Sets.newHashSet();
		for (Entity entity : others) {
			links.add(buildLink(entity));
		}
		set(links);
	}

	public Boolean add(Entity entity) {
		checkNotNull(entity);
		return add(buildLink(entity));
	}

	protected T buildLink(Entity entity) {
		T link = this.entity.getSession().create(type);
		Side side = getThisSide(link);
		link.set(side, this.entity);
		link.set(Side.opposite(side), entity);
		return link;
	}

	public Boolean remove(Entity entity) {
		checkNotNull(entity);
		T link = alias(type);
		Side side = getThisSide(link);
		BooleanBuilder booleanBuilder = new BooleanBuilder($(link.get(side))
				.eq(this.entity)).and($(link.get(Side.opposite(side))).eq(
				entity));
		T toDelete = from($(link), get()).where(booleanBuilder).uniqueResult(
				$(link));
		return toDelete == null ? false : remove(toDelete);
	}

	private Side getThisSide(Link<L, R> link) {
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
