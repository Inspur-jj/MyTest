package com.iris.pb.service;

import com.iris.pb.dto.PbEquipment2Dto;
import com.iris.pb.dto.PbEquipmentDto;
import com.iris.pb.dto.PbGroupImportDto;
import com.iris.pb.dto.PbImportDto;
import com.iris.pb.dto.PbInfoImportDto;
import com.iris.pb.dto.PbMbConstImportDto;
import com.iris.pb.dto.PbMbFlowImportDto;
import com.iris.pb.dto.PbTalentIntroductionDto;
import com.iris.pb.dto.PbTalentTrainingDto;

/**
 * 用来选择 解析EXCEL 模版的string数组
 * 
 * @author HK
 * 
 */
public class PbImportUtils {
	/**
	 * 获得解析基地平台excel 的模版 0=重点实验室基本信息 ,1=固定人员.....等等，与指定模版相对应
	 * 
	 * @param i
	 * @return
	 */
	public static String[] getDtoStringArr(int i) {
		String[] a = null;
		switch (i) {
		case 0:
			String b = new String(
					"zh_name,location,create_date,org_name,area_size,subject_area,research_direction,psn_name,psn_tel,psn_tel2"
							+ ",psn_email,contact_psn,contact_mobile,contact_tel,contact_email,http,lab_direction,lab_experience"
							+ ",year_achievement,lab_talenttraining,opinion,year_summary");
			a = b.split(",");
			b = null;
			break;
		case 1:
			String b1 = new String("org_name,pb_zhname,psn_name,gender,birthday,prof_title_name,prof_title_level_name,"
					+ "educational_name,job_category,marjor,talent_level_name,mobile,tel,email");
			a = b1.split(",");
			b1 = null;
			break;
		case 2:// 流动人员
			String b2 = new String("org_name,pb_zhname,psn_flow_name,prof_title_level_name,start_date,"
					+ "end_date,work_month,educational_name,is_in_flow,is_domestic");
			a = b2.split(",");
			b2 = null;
			break;
		case 3:// 高层次人才
			String b3 = new String(
					"org_name,pb_zhname,training_year,psn_train_name,educational_name,is_talant,talent_level_name,desc");
			a = b3.split(",");
			b3 = null;
			break;
		case 4:// 人才引进
			String b4 = new String("org_name,pb_zhname,talant_year,psn_name,educational_name,glory,glory_year,desc");
			a = b4.split(",");
			b4 = null;
			break;
		case 5:// 团队建设
			String b5 = new String(
					"org_name,pb_zhname,group_year,project_name,research_direction,psn_name,group_type,desc");
			a = b5.split(",");
			b5 = null;
			break;
		case 6:// 10万以下设备
			String b6 = new String("org_name,pb_zhname,equipment_name,num,price,sum_price");
			a = b6.split(",");
			b6 = null;
			break;
		case 7:// 10万以上设备
			String b7 = new String(
					"org_name,pb_zhname,equipment_name,num,price,sum_price,work_time,work_time_out,timing");
			a = b7.split(",");
			b7 = null;
			break;
		case 8:
			String b8 = new String(
					"zh_name,create_date,org_name,area_size,subject_area,lab_direction,psn_code,contact_psn,"
							+ "contact_mobile,contact_email,http,lab_direction,lab_experience,lab_talenttraining");
			a = b8.split(",");
			b8 = null;
			break;
		case 9:
			String b9 = new String(
					"zh_name,create_date,org_name,area_size,subject_area,lab_direction,psn_code,contact_psn,"
							+ "contact_mobile,contact_email,http,lab_direction,lab_experience,lab_talenttraining");
			a = b9.split(",");
			b9 = null;
			break;
		case 10:
			String b10 = new String(
					"zh_name,create_date,org_name,area_size,subject_area,lab_direction,psn_code,contact_psn,"
							+ "contact_mobile,contact_email,http,lab_direction,lab_experience,lab_talenttraining");
			a = b10.split(",");
			b10 = null;
			break;
		case 11:
			String b11 = new String(
					"zh_name,create_date,org_name,area_size,subject_area,lab_direction,psn_code,contact_psn,"
							+ "contact_mobile,contact_email,http,lab_direction,lab_experience,lab_talenttraining");
			a = b11.split(",");
			b11 = null;
			break;
		case 12:
			String b12 = new String(
					"zh_name,create_date,org_name,area_size,subject_area,lab_direction,psn_code,contact_psn,"
							+ "contact_mobile,contact_email,http,lab_direction,lab_experience,lab_talenttraining");
			a = b12.split(",");
			b12 = null;
			break;
		case 13:
			String b13 = new String(
					"zh_name,create_date,org_name,area_size,subject_area,lab_direction,psn_code,contact_psn,"
							+ "contact_mobile,contact_email,http,lab_direction,lab_experience,lab_talenttraining");
			a = b13.split(",");
			b13 = null;
			break;
		case 14:
			String b14 = new String(
					"zh_name,create_date,org_name,area_size,subject_area,lab_direction,psn_code,contact_psn,"
							+ "contact_mobile,contact_email,http,lab_direction,lab_experience,lab_talenttraining");
			a = b14.split(",");
			b14 = null;
			break;
		case 15:
			String b15 = new String(
					"zh_name,create_date,org_name,area_size,subject_area,lab_direction,psn_code,contact_psn,"
							+ "contact_mobile,contact_email,http,lab_direction,lab_experience,lab_talenttraining");
			a = b15.split(",");
			b15 = null;
			break;
		case 16:
			String b16 = new String(
					"zh_name,create_date,org_name,area_size,subject_area,lab_direction,psn_code,contact_psn,"
							+ "contact_mobile,contact_email,http,lab_direction,lab_experience,lab_talenttraining");
			a = b16.split(",");
			b16 = null;
			break;
		case 17:
			String b17 = new String(
					"zh_name,create_date,org_name,area_size,subject_area,lab_direction,psn_code,contact_psn,"
							+ "contact_mobile,contact_email,http,lab_direction,lab_experience,lab_talenttraining");
			a = b17.split(",");
			b17 = null;
			break;
		case 18:
			String b18 = new String(
					"zh_name,create_date,org_name,area_size,subject_area,lab_direction,psn_code,contact_psn,"
							+ "contact_mobile,contact_email,http,lab_direction,lab_experience,lab_talenttraining");
			a = b18.split(",");
			b18 = null;
			break;
		default:
			break;
		}
		return a;
	}

	public static PbImportDto getDto(int i) {
		switch (i) {
		case 0:
			return new PbInfoImportDto();
		case 1:
			return new PbMbConstImportDto();
		case 2:
			return new PbMbFlowImportDto();
		case 3:
			return new PbTalentTrainingDto();
		case 4:
			return new PbTalentIntroductionDto();
		case 5:
			return new PbGroupImportDto();
		case 6:
			return new PbEquipmentDto();
		case 7:
			return new PbEquipment2Dto();
		default:
			break;
		}
		return null;

	}

	/**
	 * 对于固定的模版可以这样写简单工厂方法
	 * 
	 * @param i
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static Class getFactClass(int i) {
		switch (i) {
		case 0:
			return PbInfoImportDto.class;
		case 1:
			return PbMbConstImportDto.class;
		case 2:
			return PbMbFlowImportDto.class;
		case 3:
			return PbTalentTrainingDto.class;
		case 4:
			return PbTalentIntroductionDto.class;
		case 5:
			return PbGroupImportDto.class;
		case 6:
			return PbEquipmentDto.class;
		case 7:
			return PbEquipment2Dto.class;
		default:
			break;
		}

		return null;

	}
}
