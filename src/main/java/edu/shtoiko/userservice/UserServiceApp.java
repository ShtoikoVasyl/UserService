package edu.shtoiko.userservice;

import edu.shtoiko.userservice.model.entity.Role;
import edu.shtoiko.userservice.model.entity.User;
import edu.shtoiko.userservice.model.enums.AccountStatus;
import edu.shtoiko.userservice.model.enums.TransactionStatus;
import edu.shtoiko.userservice.repository.RoleRepository;
import edu.shtoiko.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class UserServiceApp {

    public static void main(String[] args) {
//        SpringApplication.run(UserServiceApp.class, args);


        var user = new User();
        user.setEmail("email");
        user.setPassword("password");
        user.setFirstName("John");
        user.setLastName("Dou");

        var userSecond = new User();
        userSecond.setEmail("emailSecond");
        userSecond.setPassword("passwordSecond");
        userSecond.setFirstName("Johan");
        userSecond.setLastName("Doubush");

        Currency currency = new Currency();
        currency.setCode("UAH");
        currency.setFullName("Ukrainian hryvna");

        var account = new CurrentAccount();
        account.setAccountName("Second's account");
        account.setAccountStatus(AccountStatus.OK);
        account.setOwner(userSecond);
        account.setAmount(1000L);
        account.setCurrency(currency);

        List<CurrentAccount> list = new ArrayList<>();
        list.add(account);
        userSecond.setCurrentAccountList(list);

        ApplicationContext context = SpringApplication.run(UserServiceApp.class, args);

        // Отримайте бін з контексту за його ім'ям або класом
        UserService userServiceImplementation = context.getBean(UserService.class);
        AccountService accountService = context.getBean(AccountService.class);
        CurrencyRepository currencyRepository = context.getBean(CurrencyRepository.class);
        AccountRepository accountRepository = context.getBean(AccountRepository.class);
//        DatabaseInitializer databaseInitializer = context.getBean(DatabaseInitializer.class);
//        databaseInitializer.init();

        TransactionRepository transactionRepository = context.getBean(TransactionRepository.class);

//        MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://shtoiko:1123@localhost:27017/bs"));
//        MongoDatabase db = mongoClient.getDatabase("bs");
//        MongoCollection<Document> collection = db.getCollection("transactions");
//
//        collection.insertOne(new Document("zxcv", 6));


        Transaction transaction = Transaction.builder()
//                        .id(1L)
                .transactionStatus(TransactionStatus.IN_PROGRESS)
                .date(LocalDateTime.now())
                .amount(800)
                .senderCurrentAccountId(1L)
                .receiverCurrentAccountId(2L)
                .build();
//        transactionRepository.save(transaction);
//        System.out.println("save");

//        System.out.println(transactionRepository.findOne());
//        System.out.println("print");

        userServiceImplementation.create(user);
        userServiceImplementation.create(userSecond);
        System.out.println(user.getId());

        currencyRepository.save(currency);
        System.out.println(accountService.create(account));
        accountRepository.save(account);


        System.out.println(userSecond.getId());

        System.out.println("get user by id 2 :" + userServiceImplementation.readById(2));
    }


        @Autowired
        private RoleRepository roleRepository;

        @Bean
        public CommandLineRunner commandLineRunner () {
            return args -> {
                roleRepository.saveAll(Arrays.asList(
                        new Role(1, "User"),
                        new Role(2, "Admin")
                ));
            };

        }

    }
