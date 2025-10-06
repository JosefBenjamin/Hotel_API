package app.security.entities;

import app.security.hashing.PasswordHasher;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;


@Entity //JPA
// @NamedQueries(@NamedQuery(name = "User.deleteAllRows", query = "DELETE from User")) //JPA
@Getter //lombok
@Setter //lombok
@NoArgsConstructor //lombok
@AllArgsConstructor //lombok
@ToString //lombok
@Builder //lombok
@Table(name = "users") //JPA
public class User implements ISecurityUser {
    private static final PasswordHasher passwordHasher = new PasswordHasher();

    @Id //JPA
    @Column(nullable = false, length = 20, unique = true)
    private String username;

    @Column(nullable = false, length = 50)
    private String password;

    @JoinTable(name = "user_roles", //Creates new table called user_roles
            //Join tables at and creates columns user_name and role_name, and references username and name respectively
            joinColumns = {@JoinColumn(name = "user_name", referencedColumnName = "username")},
            inverseJoinColumns = {@JoinColumn(name = "role_name", referencedColumnName = "name")})
     @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @Builder.Default
    private Set<Role> roles = new HashSet<>();


    public Set<String> getRolesAsStrings() {
        if (roles.isEmpty()) {
            return null;
        }

        Set<String> rolesAsStrings = new HashSet<>();
        roles.forEach((r) -> rolesAsStrings.add(r.getName()));
        return rolesAsStrings;
    }

    //TODO: Constructors
    public User(String userName, String userPass) {
        this.username = userName;
        this.password = passwordHasher.hashPassFirstTime(userPass);
    }

    public User(String userName, Set<Role> roleEntityList) {
        this.username = userName;
        this.roles = roleEntityList;
    }


    @Override
    public boolean verifyPass(String plainPassword) {
        return passwordHasher.checkPw(plainPassword, this.password);
    }

    @Override
    public void addRole(Role role) {
        if (role == null) {
            return;
        }
        roles.add(role);
        role.getUsers().add(this);
    }

    public void removeRole(String userRole) {
        roles.stream()
                .filter((r) ->
                {
                    return r.getName().equalsIgnoreCase(userRole);
                })
                .findFirst()
                .ifPresent(role -> {
                    roles.remove(role);
                    role.getUsers().remove(this);
                });
    }

}
