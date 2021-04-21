package com.example.demo.mapper;

import com.example.demo.bean.OnlineStock;
import com.example.demo.example.OnlineStockExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OnlineStockMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_online_stock
     *
     * @mbg.generated Sun Jan 10 14:32:59 CST 2021
     */
    long countByExample(OnlineStockExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_online_stock
     *
     * @mbg.generated Sun Jan 10 14:32:59 CST 2021
     */
    int deleteByExample(OnlineStockExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_online_stock
     *
     * @mbg.generated Sun Jan 10 14:32:59 CST 2021
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_online_stock
     *
     * @mbg.generated Sun Jan 10 14:32:59 CST 2021
     */
    int insert(OnlineStock record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_online_stock
     *
     * @mbg.generated Sun Jan 10 14:32:59 CST 2021
     */
    int insertSelective(OnlineStock record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_online_stock
     *
     * @mbg.generated Sun Jan 10 14:32:59 CST 2021
     */
    List<OnlineStock> selectByExample(OnlineStockExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_online_stock
     *
     * @mbg.generated Sun Jan 10 14:32:59 CST 2021
     */
    OnlineStock selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_online_stock
     *
     * @mbg.generated Sun Jan 10 14:32:59 CST 2021
     */
    int updateByExampleSelective(@Param("record") OnlineStock record, @Param("example") OnlineStockExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_online_stock
     *
     * @mbg.generated Sun Jan 10 14:32:59 CST 2021
     */
    int updateByExample(@Param("record") OnlineStock record, @Param("example") OnlineStockExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_online_stock
     *
     * @mbg.generated Sun Jan 10 14:32:59 CST 2021
     */
    int updateByPrimaryKeySelective(OnlineStock record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_online_stock
     *
     * @mbg.generated Sun Jan 10 14:32:59 CST 2021
     */
    int updateByPrimaryKey(OnlineStock record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_online_stock
     *
     * @mbg.generated Sun Jan 10 14:32:59 CST 2021
     */
    int batchInsert(@Param("list") List<OnlineStock> list);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_online_stock
     *
     * @mbg.generated Sun Jan 10 14:32:59 CST 2021
     */
    int batchInsertSelective(@Param("list") List<OnlineStock> list, @Param("selective") OnlineStock.Column ... selective);
}