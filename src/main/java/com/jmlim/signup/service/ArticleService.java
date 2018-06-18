package com.jmlim.signup.service;

//@Service
public class ArticleService {
/*
	@Autowired
	private AccountRepository accountRepo;
	@Autowired
	private AccountRoleRepository accountRoleRepo;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private ModelMapper modelMapper;

	@Transactional
	public Long save(AccountDto.Create accountCreateDto) {
		Account account = modelMapper.map(accountCreateDto, Account.class);
		// 패스워드 암호화
		account.setPassword(passwordEncoder.encode(account.getPassword()));
		account.setJoinDate(new Date());
		Long id = accountRepo.save(account).getId();

		// 권한 입력
		AccountRole role = new AccountRole();
		role.setParent(account);
		role.setRole("ROLE_USER");
		accountRoleRepo.save(role);
		return id;
	}*/
}
