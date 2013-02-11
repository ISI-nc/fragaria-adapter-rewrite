package nc.isi.fragaria_adapter_rewrite.entities;

import static com.google.common.base.Preconditions.checkArgument;

import com.fasterxml.jackson.databind.node.ObjectNode;

public abstract class Link<L extends Entity, R extends Entity> extends
		AbstractEntity {

	public enum Side {
		L, R;

		public static final Side opposite(Side side) {
			switch (side) {
			case L:
				return R;
			case R:
				return L;
			default:
				throw new IllegalArgumentException();
			}
		}
	}

	public static final String L = "l";
	public static final String R = "r";

	private final Class<R> rType;
	private final Class<L> lType;

	public Link(Class<L> lType, Class<R> rType) {
		super();
		this.rType = rType;
		this.lType = lType;
	}

	public Link(Class<L> lType, Class<R> rType, ObjectNode node) {
		super(node);
		this.rType = rType;
		this.lType = lType;
	}

	public L getL() {
		return readProperty(lType, L);
	}

	public void setL(L l) {
		writeProperty(L, l);
	}

	public R getR() {
		return readProperty(rType, R);
	}

	public void setR(R r) {
		writeProperty(R, r);
	}

	public Boolean isR(Entity entity) {
		return rType.isAssignableFrom(entity.getClass());
	}

	public Boolean isL(Entity entity) {
		return lType.isAssignableFrom(entity.getClass());
	}

	public Entity get(Side side) {
		switch (side) {
		case L:
			return getL();
		case R:
			return getR();
		default:
			throw new IllegalArgumentException(side.name());
		}
	}

	public void set(Side side, Entity entity) {
		switch (side) {
		case L:
			checkArgument(isL(entity));
			setL(lType.cast(entity));
			break;
		case R:
			checkArgument(isR(entity));
			setR(rType.cast(entity));
			break;

		default:
			throw new IllegalArgumentException(side.name());
		}
	}
}
