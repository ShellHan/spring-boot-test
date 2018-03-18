
	private static final Logger logger = LoggerFactory.getLogger(MyTest.class);
	
	@Autowired
	private UserService userService;
	
	@Test
	public void getUserList(){
		ResultDTO result = userService.getAll();
		logger.info("调用 userService.getAll 返回信息：result{}",result.getData());
	}
	
}
