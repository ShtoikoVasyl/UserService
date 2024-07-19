package edu.shtoiko.userservice.model.entity;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import edu.shtoiko.userservice.model.enums.UserStatus;
import edu.shtoiko.userservice.model.account.CurrentAccount;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "users")
@Getter
@Setter
//@RequiredArgsConstructor
@AllArgsConstructor
@NoArgsConstructor
//@ToString(of = {"userId", "firstName", "secondName", "email", "password"})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @JsonManagedReference
    @OneToMany(mappedBy = "owner", cascade = CascadeType.REMOVE)
    @ToString.Exclude
    private List<CurrentAccount> currentAccountList;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
//    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "status")
    private UserStatus userStatus;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        User user = (User) o;
        return getId() != null && Objects.equals(getId(), user.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + id +
                ", firstName='" + firstName + '\'' +
                ", secondName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
