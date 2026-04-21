package com.yumefusaka.yuelivingapi;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yumefusaka.yuelivingapi.mapper.PropertyMapper;
import com.yumefusaka.yuelivingapi.mapper.UserMapper;
import com.yumefusaka.yuelivingapi.pojo.DTO.BindOwnerDTO;
import com.yumefusaka.yuelivingapi.pojo.Entity.Property;
import com.yumefusaka.yuelivingapi.pojo.Entity.User;
import com.yumefusaka.yuelivingapi.service.PropertyService;
import com.yumefusaka.yuelivingapi.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class YueLivingApiApplicationTests {

	@Autowired
	private UserService userService;

	@Autowired
	private UserMapper userMapper;

	@Autowired
	private PropertyService propertyService;

	@Autowired
	private PropertyMapper propertyMapper;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void contextLoads() {
	}

	@Test
	void jacksonShouldDeserializeSpaceSeparatedLocalDateTime() throws Exception {
		String json = """
				{
				  "username": "role-update-user",
				  "lastLoginTime": "2026-04-20 22:56:27"
				}
				""";

		User user = objectMapper.readValue(json, User.class);

		assertEquals(LocalDateTime.of(2026, 4, 20, 22, 56, 27), user.getLastLoginTime());
	}

	@Test
	void schemaShouldContainNewBusinessTablesAndColumns() throws Exception {
		String schema = Files.readString(Path.of("src/main/resources/schema.sql"));

		assertTrue(schema.contains("CREATE TABLE system_config"));
		assertTrue(schema.contains("CREATE TABLE operation_log"));
		assertTrue(schema.contains("CREATE TABLE repair_type"));
		assertTrue(schema.contains("CREATE TABLE billing_rule"));
		assertTrue(schema.contains("last_login_time"));
		assertTrue(schema.contains("owner_id"));
		assertTrue(schema.contains("repair_type_id"));
	}

	@Test
	void loginShouldAcceptUsernameOrPhoneAndRejectDisabledUser() {
		String suffix = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
		String username = "owner" + suffix;
		String phone = "138" + suffix.substring(0, 8);

		User user = new User();
		user.setUsername(username);
		user.setPassword("123456");
		user.setPhone(phone);
		user.setRoleId(1L);
		user.setStatus(1);
		userMapper.insert(user);

		try {
			assertNotNull(userService.login(username, "123456"));
			assertNotNull(userService.login(phone, "123456"));

			User disabledUser = new User();
			disabledUser.setStatus(0);
			assertThrows(RuntimeException.class, () -> userService.ensureUserEnabled(disabledUser));
		} finally {
			userMapper.delete(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
		}
	}

	@Test
	void systemAdminShouldManageManagersAndPropertyManagerShouldManageOwnersOnly() {
		assertTrue(userService.canManageUser(3L, 2L));
		assertTrue(userService.canManageUser(2L, 1L));
		assertFalse(userService.canManageUser(2L, 3L));
	}

	@Test
	void propertyBindingShouldSetOwnerSnapshotAndBindTime() {
		String suffix = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
		User owner = new User();
		owner.setUsername("propowner" + suffix);
		owner.setPassword("123456");
		owner.setPhone("139" + suffix);
		owner.setRealName("Owner " + suffix);
		owner.setRoleId(1L);
		owner.setStatus(1);
		userMapper.insert(owner);

		Property property = new Property();
		property.setBuildingNo("B" + suffix.substring(0, 2));
		property.setUnitNo("U" + suffix.substring(2, 4));
		property.setRoomNo("R" + suffix.substring(4, 8));
		property.setPropertyType("住宅");
		property.setStatus(0);
		propertyMapper.insert(property);

		try {
			propertyService.bindOwner(new BindOwnerDTO(property.getId(), owner.getId()));
			Property updatedProperty = propertyService.getById(property.getId());
			assertTrue(owner.getId().equals(updatedProperty.getOwnerId()));
			assertNotNull(updatedProperty.getOwnerNameSnapshot());
			assertNotNull(updatedProperty.getBindTime());
		} finally {
			propertyMapper.deleteById(property.getId());
			userMapper.deleteById(owner.getId());
		}
	}

	@Test
	void ownerPropertyQueryShouldOnlyReturnBoundProperties() {
		String suffix = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
		User owner = new User();
		owner.setUsername("queryowner" + suffix);
		owner.setPassword("123456");
		owner.setPhone("137" + suffix);
		owner.setRealName("Query Owner " + suffix);
		owner.setRoleId(1L);
		owner.setStatus(1);
		userMapper.insert(owner);

		Property property = new Property();
		property.setBuildingNo("Q" + suffix.substring(0, 2));
		property.setUnitNo("Q" + suffix.substring(2, 4));
		property.setRoomNo("Q" + suffix.substring(4, 8));
		property.setPropertyType("住宅");
		property.setOwnerId(owner.getId());
		property.setOwnerNameSnapshot(owner.getRealName());
		property.setStatus(1);
		propertyMapper.insert(property);

		try {
			List<Property> properties = propertyService.getPropertiesByOwnerId(owner.getId());
			assertTrue(properties.stream().allMatch(item -> owner.getId().equals(item.getOwnerId())));
		} finally {
			propertyMapper.deleteById(property.getId());
			userMapper.deleteById(owner.getId());
		}
	}

}
