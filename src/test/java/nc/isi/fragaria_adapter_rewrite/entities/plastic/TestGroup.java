package nc.isi.fragaria_adapter_rewrite.entities.plastic;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

@Entity
public class TestGroup {

	@Id
	private String name;

	@ManyToMany(mappedBy = "groups")
	private Set<TestUser> users;

	protected String getName() {
		return name;
	}

	protected void setName(String name) {
		this.name = name;
	}

	protected Set<TestUser> getUsers() {
		return users;
	}

	protected void setUsers(Set<TestUser> users) {
		this.users = users;
	}

}
