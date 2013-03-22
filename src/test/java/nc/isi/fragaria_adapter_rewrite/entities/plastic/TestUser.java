package nc.isi.fragaria_adapter_rewrite.entities.plastic;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.apache.commons.codec.digest.DigestUtils;

@Entity
public class TestUser {

	@Id
	private String login;

	@Column(name = "password")
	private String passwordHash;

	private Integer failCount;

	private Date birthDate;

	private String firstName;

	private String lastName;

	@ManyToOne
	private TestUser manager;

	@ManyToMany(mappedBy = "users")
	private Set<TestGroup> groups;

	@OneToMany(mappedBy = "manager")
	private List<TestUser> fellows;
	
	@Embedded
	private Set<TestPhone> phones;

	@Transient
	private String password;

	public void setPassword(String password) {
		this.passwordHash = DigestUtils.sha256Hex(password);
		this.password = password;
	}

	// ---------------------------------------------------------------
	// simple accessords
	//

	public String getPassword() {
		return password;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

	public Integer getFailCount() {
		return failCount;
	}

	public void setFailCount(Integer failCount) {
		this.failCount = failCount;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Set<TestGroup> getGroups() {
		return groups;
	}

	public void setGroups(Set<TestGroup> groups) {
		this.groups = groups;
	}

	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

}
