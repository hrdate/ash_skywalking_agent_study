package org.example.app.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author : Ashiamd email: ashiamd@foxmail.com
 * @date : 2023/12/23 10:04 PM
 */
@Data
@TableName("agent_user")
public class UserEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String userName;
    private String pwd;
}
