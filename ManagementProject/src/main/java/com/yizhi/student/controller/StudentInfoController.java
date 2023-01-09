package com.yizhi.student.controller;

import com.yizhi.common.annotation.Log;
import com.yizhi.common.utils.PageUtils;
import com.yizhi.common.utils.Query;
import com.yizhi.common.utils.R;
import com.yizhi.student.domain.StudentInfoDO;
import com.yizhi.student.service.StudentInfoService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 生基础信息表
 */

@Controller
@RequestMapping("/student/studentInfo")
public class StudentInfoController {

	@Autowired
	private StudentInfoService studentInfoService;

	@Log("学生信息保存")
	@ResponseBody
	@PostMapping("/save")
	@RequiresPermissions("student:studentInfo:add")
	public R save(@Valid StudentInfoDO studentInfoDO){
		String studentId = studentInfoDO.getStudentId();
		if(studentId == null || studentId.equals("") || StringUtils.isEmpty(studentId) || studentId.equals(" ")){
			return R.error();
		}
		try {
			int save = studentInfoService.save(studentInfoDO);
			if (save < 0) {
				throw new RuntimeException();
			}
		} catch (Exception e) {
			return R.error();
		}
		return R.ok();
	}

	/**
	 * 可分页 查询
	 */
	@ResponseBody
	@GetMapping("/list")
	@RequiresPermissions("student:studentInfo:studentInfo")
	public PageUtils list(@RequestParam Map<String, Object> params){
		Object collegeid = params.get("collegeid");
		if(collegeid != null){
			params.put("tocollegeId",collegeid);
		}
		Query query = new Query(params);
		List<StudentInfoDO> studentList = studentInfoService.list(query);
		int total = studentInfoService.count(query);
		PageUtils pageUtils = new PageUtils(studentList, total, query.getCurrPage(), query.getPageSize());
		if (pageUtils.getRows().size() == 0){
			List<String> arrays = new ArrayList<>();
			arrays.add("查询失败");
			pageUtils.setRows(arrays);
		}
		return pageUtils;
	}


	/**
	 * 修改
	 */
	@Log("学生基础信息表修改")
	@ResponseBody
	@PostMapping("/update")
	@RequiresPermissions("student:studentInfo:edit")
	public R update(@Valid StudentInfoDO studentInfo){
		int update = studentInfoService.update(studentInfo);
		if (update > 0) {
			return R.ok().put("studentInfo",studentInfo);
		}
		return R.error();
	}

	/**
	 * 删除
	 */
	@Log("学生基础信息表删除")
	@PostMapping( "/remove")
	@ResponseBody
	@RequiresPermissions("student:studentInfo:remove")
	public R remove(Integer id){
		if(id == null){
			return R.error();
		}
		int remove = studentInfoService.remove(id);
		if (remove > 0) {
			return R.ok();
		}
		return R.error();
	}

	/**
	 * 批量删除
	 */
	@Log("学生基础信息表批量删除")
	@PostMapping( "/batchRemove")
	@ResponseBody
	@RequiresPermissions("student:studentInfo:batchRemove")
	public R remove(@RequestParam("ids[]") Integer[] ids){
		int i = studentInfoService.batchRemove(ids);
		if (i > 0) {
			return R.ok();
		}
		return R.error();
	}


	//前后端不分离 客户端 -> 控制器-> 定位视图
	/**
	 * 学生管理 点击Tab标签 forward页面
	 */
	@GetMapping()
	@RequiresPermissions("student:studentInfo:studentInfo")
	String StudentInfo(){
		return "student/studentInfo/studentInfo";
	}

	/**
	 * 更新功能 弹出View定位
	 */
	@GetMapping("/edit/{id}")
	@RequiresPermissions("student:studentInfo:edit")
	String edit(@PathVariable("id") Integer id,Model model){
		StudentInfoDO studentInfo = studentInfoService.get(id);
		model.addAttribute("studentInfo", studentInfo);
		return "student/studentInfo/edit";
	}

	/**
	 * 学生管理 添加学生弹出 View
	 */
	@GetMapping("/add")
	@RequiresPermissions("student:studentInfo:add")
	String add(){
		return "student/studentInfo/add";
	}

}//end class
