package nc.isi.fragaria_adapter_rewrite;

import java.util.List;

import junit.framework.TestCase;

import com.google.common.reflect.TypeToken;

public class TestTypeToken extends TestCase {

	public void testTypeToken() {
		TypeToken<List<String>> typeToken = new TypeToken<List<String>>() {
		};
		System.out.println(typeToken.getType());
		System.out.println(typeToken.getComponentType());
		System.out.println(typeToken.getRawType());
		System.out.println(typeToken.getTypes());
		try {
			System.out.println(typeToken
					.method(typeToken.getRawType().getMethod("get", int.class))
					.getReturnType().getRawType());
		} catch (NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
