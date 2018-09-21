package com.itunion.springbootstarterdruid.mapper;



import com.itunion.springbootstarterdruid.model.UserInfo;
import com.itunion.springbootstarterdruid.web.dto.UserInfoReqListDto;
import com.itunion.springbootstarterdruid.web.dto.UserInfoDto;
import org.springframework.stereotype.Repository;


import java.util.List;
@Repository
public interface UserInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(UserInfo record);

    int insertSelective(UserInfo record);

    UserInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(UserInfo record);

    int updateByPrimaryKey(UserInfo record);

    /**
     * 获取用户信息集合
     * @return
     */
    List<UserInfoDto> getUserInfoList(UserInfoReqListDto userInfoReqListDto);
}