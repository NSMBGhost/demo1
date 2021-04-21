package com.example.demo.mapper;

import com.example.demo.bean.PauseStock;
import com.example.demo.example.PauseStockExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PauseStockMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_pause_stock
     *
     * @mbg.generated Mon Jan 11 16:30:36 CST 2021
     */
    long countByExample(PauseStockExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_pause_stock
     *
     * @mbg.generated Mon Jan 11 16:30:36 CST 2021
     */
    int deleteByExample(PauseStockExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_pause_stock
     *
     * @mbg.generated Mon Jan 11 16:30:36 CST 2021
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_pause_stock
     *
     * @mbg.generated Mon Jan 11 16:30:36 CST 2021
     */
    int insert(PauseStock record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_pause_stock
     *
     * @mbg.generated Mon Jan 11 16:30:36 CST 2021
     */
    int insertSelective(PauseStock record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_pause_stock
     *
     * @mbg.generated Mon Jan 11 16:30:36 CST 2021
     */
    List<PauseStock> selectByExample(PauseStockExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_pause_stock
     *
     * @mbg.generated Mon Jan 11 16:30:36 CST 2021
     */
    PauseStock selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_pause_stock
     *
     * @mbg.generated Mon Jan 11 16:30:36 CST 2021
     */
    int updateByExampleSelective(@Param("record") PauseStock record, @Param("example") PauseStockExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_pause_stock
     *
     * @mbg.generated Mon Jan 11 16:30:36 CST 2021
     */
    int updateByExample(@Param("record") PauseStock record, @Param("example") PauseStockExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_pause_stock
     *
     * @mbg.generated Mon Jan 11 16:30:36 CST 2021
     */
    int updateByPrimaryKeySelective(PauseStock record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_pause_stock
     *
     * @mbg.generated Mon Jan 11 16:30:36 CST 2021
     */
    int updateByPrimaryKey(PauseStock record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_pause_stock
     *
     * @mbg.generated Mon Jan 11 16:30:36 CST 2021
     */
    int batchInsert(@Param("list") List<PauseStock> list);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_pause_stock
     *
     * @mbg.generated Mon Jan 11 16:30:36 CST 2021
     */
    int batchInsertSelective(@Param("list") List<PauseStock> list, @Param("selective") PauseStock.Column ... selective);
}
