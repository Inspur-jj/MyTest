package com.iris.pb.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.hibernate.SQLQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.iris.core.exception.ServiceException;
import com.iris.core.security.utils.SecurityUtils;
import com.iris.grid.datatable.DtConditionContainer;
import com.iris.grid.datatable.DtPageContainer;
import com.iris.grid.mybatis.MyBatisDao;
import com.iris.pb.dao.PbInfoDao;
import com.iris.pb.dto.PbImportDto;
import com.iris.pb.dto.PbInfoImportDto;
import com.iris.pb.model.PbInfo;
import com.iris.security.file.dao.ConstFileDictionaryDao;
import com.iris.security.system.dao.PersonDao;
import com.iris.utils.CollectionUtils;
import com.iris.utils.IrisStringUtils;
import com.iris.utils.MoneyUtils;
import com.iris.utils.XMLHelper;
import com.iris.utils.date.DateUtils;
import com.iris.utils.excel.Excel2EntityConfig;

@Service("importPbService")
@Transactional(rollbackFor = Exception.class)
public class ImportPbServiceImpl implements ImportPbService {

	/**
	 * 
	 */
	private static final long serialVersionUID = -322695513374706495L;

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private MyBatisDao<Map<String, Object>> myBatisDao;

	@Autowired
	private PersonDao personDao;

	@Autowired
	private PbInfoDao pbInfoDao;

	@Autowired
	private ConstFileDictionaryDao constFileDictionaryDao;

	@Override
	public DtPageContainer getImportHistoryList(DtConditionContainer conditionContainer) {
		return myBatisDao.getSearchPage("Pb.importFileList", conditionContainer);
	}

	/**
	 * 多sheet 多模版excel解析 如果模版Excel文件有修改 只需修改PbImportUtils这个类
	 * 
	 * @author HK
	 */
	@Override
	public List<List<? extends PbImportDto>> analyzeImpFile(File importPrpFile) throws ServiceException {
		List<List<? extends PbImportDto>> lists = new ArrayList<List<? extends PbImportDto>>();

		InputStream input = null;
		try {
			input = new FileInputStream(importPrpFile);
			Excel2EntityConfig excel2EntityConfig = new Excel2EntityConfig();
			HSSFWorkbook wb = new HSSFWorkbook(input);
			input = null;
			/* 对于给定的模版 获得sheet个数，然后循环解析 */
			int sheets = wb.getNumberOfSheets();
			List<PbImportDto> list = null;
			for (int i = 0; i < sheets - 11; i++) {// 先测试前8个

				excel2EntityConfig.setColumns(PbImportUtils.getDtoStringArr(i));
				excel2EntityConfig.setCurrPosittion(2);
				excel2EntityConfig.setColStartPosittion(1);
				ExcelReaderHK<PbImportDto> excel = new ExcelReaderHK<PbImportDto>();
				excel.setExcel2EntityConfig(excel2EntityConfig);
				input = new FileInputStream(importPrpFile);
				excel.InitExcelReader(input);
				excel.setCurrSheet(i);

				list = new ArrayList<PbImportDto>();
				PbImportDto entity = PbImportUtils.getDto(i);
				excel.setEntity(entity);
				try {
					entity = excel.readLine();
				} catch (ArrayIndexOutOfBoundsException e) {
					e.printStackTrace();
				}
				while (entity != null) {
					if (checkBlankLine(entity)) {
						if (checkBisLogic(entity)) {
							entity.setCheckMsg(PbImportDto.CHECK_SUCCESS);
						} else {
							if (StringUtils.isBlank(entity.getCheckMsg())) {
								entity.setCheckMsg(PbImportDto.CHECK_FAIL);
							}
						}
						list.add(entity);
					}
					entity = PbImportUtils.getDto(i);
					excel.setEntity(entity);
					try {
						entity = excel.readLine();
					} catch (ArrayIndexOutOfBoundsException e) {
						e.printStackTrace();
					}
				}

				lists.add(list);
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lists;
	}

	/**
	 * 检测空行
	 */
	private boolean checkBlankLine(PbImportDto entity) {
		if (entity == null) {
			return false;
		}
		return true;
	}

	/**
	 * Excel表业务逻辑检验 通用版（不适合具体的有需要特殊检测的字段的业务）
	 * 
	 * @author HK
	 */
	@SuppressWarnings("unused")
	private boolean checkBisLogic(PbImportDto pbDto) throws Exception {
		if (pbDto == null) {
			return false;
		}
		List<Object> paramList = new ArrayList<Object>();
		SQLQuery resultList = null;

		StringBuilder errorField = new StringBuilder();
		StringBuilder errorMsg = new StringBuilder();
		boolean flag = true;

		Field[] fields = pbDto.getClass().getDeclaredFields();
		for (Field field : fields) {
			String tempName = field.getName();
			if (tempName.contains("date") || tempName.equals("birthday")) {// 检测日期格式
				String methodName = "get" + A2UpperCase(tempName);
				Method sm = pbDto.getClass().getDeclaredMethod(methodName);
				String date = (String) sm.invoke(pbDto);
				if (!DateUtils.isDate(date)) {
					errorField.append(tempName + ",");
					errorMsg.append("不是合法日期格式，");
				}
				continue;
			}
			if (tempName.contains("psn_name")) {// 检测系统中是否有此人
				String methodName = "get" + A2UpperCase(tempName);
				Method sm = pbDto.getClass().getDeclaredMethod(methodName);
				String psn_name = (String) sm.invoke(pbDto);
				if (psn_name != null) {
					psn_name = psn_name.trim();
				}
				paramList.clear();
				paramList.add(psn_name);
				resultList = personDao.createSqlQuery("select psn_code from person where zh_name=?", paramList);
				if (CollectionUtils.isEmpty(resultList.list())) {
					errorField.append(tempName + ",");
					errorMsg.append("系统不存在相关人员信息，");
				}
				continue;
			}
			if (tempName.equals("pb_zhname") || tempName.equals("zh_name")) {// 基地平台特殊字段 实验室名称
				String methodName = "get" + A2UpperCase(tempName);
				Method sm = pbDto.getClass().getDeclaredMethod(methodName);
				String pb_zhname = (String) sm.invoke(pbDto);
				if (StringUtils.isBlank(pb_zhname)) {
					errorField.append(tempName + ",");
					errorMsg.append("实验室名称不能为空，");
				}
				continue;
			}
			if (tempName.contains("_year")) {// 包含year的字段在系统中一般指年度
				String methodName = "get" + A2UpperCase(tempName);
				Method sm = pbDto.getClass().getDeclaredMethod(methodName);
				String year = (String) sm.invoke(pbDto);
				if (!NumberUtils.isNumber(year)) {
					errorField.append(tempName + ",");
					errorMsg.append("不是数字格式，");
				}
				continue;
			}
			if (tempName.contains("size") || tempName.contains("money") || tempName.contains("price")) {// size面积,money金额
				String methodName = "get" + A2UpperCase(tempName);
				Method sm = pbDto.getClass().getDeclaredMethod(methodName);
				String number = (String) sm.invoke(pbDto);
				if (!NumberUtils.isNumber(number) && !checkAmt(number)) {
					errorField.append(tempName + ",");
					errorMsg.append("不是数字格式，或者整数位大于5位，小数位长多大于6位");
				}

				number = MoneyUtils.formatMoney(number, 6, false, false, false, true, 2);
				sm = pbDto.getClass().getDeclaredMethod("set" + A2UpperCase(tempName), String.class);
				sm.invoke(pbDto, number);

				continue;
			}
		}
		pbDto.setErrorFields(errorField.toString());
		pbDto.setErrMsg(errorMsg.toString());
		if (StringUtils.isNotBlank(errorField)) {
			flag = false;
		}
		return flag;
	}

	private boolean checkAmt(String amt) {
		boolean flag = true;
		BigDecimal money = new BigDecimal(amt);
		String pointAmt = amt.substring(amt.indexOf(".") + 1);
		if (money.compareTo(new BigDecimal("99999.999999")) > 0 || money.compareTo(new BigDecimal("0")) < 0
				|| pointAmt.length() > 6) {
			flag = false;
		}
		return flag;
	}

	/***
	 * 将指定英文字符串首字母大写
	 * 
	 * @param filed
	 * @return 首字母大写后的字符串
	 */
	private String A2UpperCase(String filed) {
		return filed.substring(0, 1).toUpperCase() + filed.substring(1, filed.length());
	}

	@Override
	public Long saveImpPbDto(PbImportDto pbDto, String fileCode) throws DocumentException, NumberFormatException,
			ServiceException {
		/**
		 * 如果是pbinfo 则构建XMl
		 */
		if (pbDto instanceof PbInfoImportDto) {
			Long key_Code = buildXmlAndSave((PbInfoImportDto) pbDto, fileCode);
			return key_Code;
		}
		/**
		 * 
		 */
		return null;
	}

	@SuppressWarnings("rawtypes")
	private Long buildXmlAndSave(PbInfoImportDto pbDto, String fileCode) throws DocumentException,
			NumberFormatException, ServiceException {

		String xmlData = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><data></data>";
		Document doc = DocumentHelper.parseText(xmlData);
		Element data = (Element) doc.selectObject("/data");
		Element pbNode = data.addElement("pb");
		Element basicInfo = pbNode.addElement("basic_info");

		// 实验室名称
		basicInfo.addElement("zh_name").addText(IrisStringUtils.formateStrXml(pbDto.getZh_name()));
		basicInfo.addElement("province_value");
		basicInfo.addElement("province_name");
		basicInfo.addElement("city_value");
		basicInfo.addElement("city_name");
		basicInfo.addElement("pb_level_value");
		basicInfo.addElement("pb_level_name");
		basicInfo.addElement("pb_type_value").addText("1");
		basicInfo.addElement("pb_type_name").addText("重点实验室");
		basicInfo.addElement("economic_business_value");
		basicInfo.addElement("economic_business_name");
		basicInfo.addElement("composition_mode_value");
		basicInfo.addElement("composition_mode_name");
		String establish_date = IrisStringUtils.formateStrXml(pbDto.getCreate_date());
		if (DateUtils.isDate(establish_date)) {
			basicInfo.addElement("establish_date").addText(establish_date);
		} else {
			basicInfo.addElement("establish_date");
		}
		basicInfo.addElement("dept_code_value").addText("1");
		basicInfo.addElement("dept_code_name").addText("三峡大学科研管理系统");
		basicInfo.addElement("area_size").addText(IrisStringUtils.formateStrXml(pbDto.getArea_size()));
		basicInfo.addElement("subject_area_value");
		basicInfo.addElement("subject_area_name").addText(IrisStringUtils.formateStrXml(pbDto.getSubject_area()));
		basicInfo.addElement("research_direction")
				.addText(IrisStringUtils.formateStrXml(pbDto.getResearch_direction()));
		basicInfo.addElement("psn_name").addText(IrisStringUtils.formateStrXml(pbDto.getPsn_name()));
		SQLQuery resultList = null;
		List<Object> paramList = new ArrayList<Object>();
		paramList.add(pbDto.getPsn_name());
		resultList = personDao.createSqlQuery("select psn_code from person where zh_name=?", paramList);
		String personCode = "";
		if (!CollectionUtils.isEmpty(resultList.list())) {
			Object o = resultList.list().get(0);
			personCode = o.toString();
		}
		basicInfo.addElement("psn_code").addText(IrisStringUtils.formateStrXml(personCode));
		basicInfo.addElement("psn_tel").addText(IrisStringUtils.formateStrXml(pbDto.getPsn_tel()));
		basicInfo.addElement("psn_mobile").addText(IrisStringUtils.formateStrXml(pbDto.getPsn_tel2()));
		basicInfo.addElement("psn_email").addText(IrisStringUtils.formateStrXml(pbDto.getPsn_email()));

		basicInfo.addElement("contact_psn").addText(IrisStringUtils.formateStrXml(pbDto.getContact_psn()));
		basicInfo.addElement("contact_tel").addText(IrisStringUtils.formateStrXml(pbDto.getContact_tel()));
		basicInfo.addElement("contact_mobile").addText(IrisStringUtils.formateStrXml(pbDto.getContact_mobile()));
		basicInfo.addElement("contact_email").addText(IrisStringUtils.formateStrXml(pbDto.getContact_email()));

		basicInfo.addElement("psn_code").addText(IrisStringUtils.formateStrXml(pbDto.getHttp()));
		basicInfo.addElement("fund_no");
		basicInfo.addElement("fund");
		basicInfo.addElement("budget");
		basicInfo.addElement("laboratory_location").addText(pbDto.getLab_direction());
		basicInfo.addElement("major_development").addText(pbDto.getLab_experience());
		basicInfo.addElement("achievement_cb").addText(pbDto.getYear_achievement());
		basicInfo.addElement("talent_development").addText(pbDto.getLab_talenttraining());
		basicInfo.addElement("suggestion").addText(pbDto.getOpinion());
		basicInfo.addElement("minutes_of_meeting");

		String xml = doc.asXML();
		Long pb_id = null;
		Long createPsn = null;
		Date createDate = null;
		PbInfo pb = null;
		List<PbInfo> templist = pbInfoDao.getByPbName(IrisStringUtils.formateStrXml(pbDto.getZh_name()));
		if (!CollectionUtils.isEmpty(templist)) {
			pb = templist.get(0);
			pb_id = pb.getPbId();
			createDate = pb.getCreateDate();
			createPsn = pb.getCreatePsn();
		}
		if (pb != null) {
			Document oldDoc = XMLHelper.parseDocument(pb.getXml());
			Document newDoc = XMLHelper.parseDoc(XMLHelper.parseW3cDoc(xml));
			Map<String, Object> compare = XMLHelper.compareXml(oldDoc, newDoc);
			if (compare.get("same") != null && (Boolean) compare.get("same")) {
				return pb.getPbId();
			}
		}

		pb = new PbInfo();

		pb.setAreaSize(pbDto.getArea_size());
		pb.setContactEmail(pbDto.getContact_email());
		pb.setContactMobile(pbDto.getContact_mobile());
		pb.setContactPsn(pbDto.getContact_psn());
		pb.setContactTel(pbDto.getContact_tel());
		if (DateUtils.isDate(establish_date)) {
			try {
				pb.setEstablishDate(new SimpleDateFormat("yyyy-MM-dd").parse(establish_date));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		pb.setHttp(pbDto.getHttp());
		pb.setResearchDirection(pbDto.getLab_direction());
		pb.setMajorDevelopment(pbDto.getLab_experience());
		pb.setTalentDevelopment(pbDto.getLab_talenttraining());
		pb.setProvince(pbDto.getLocation());
		pb.setSuggestion(pbDto.getOpinion());
		pb.setDeptCode(Long.valueOf("1"));
		pb.setPbType(Long.valueOf("1"));// 基地类别
		pb.setPbLevel(Long.valueOf("2"));// 基地级别
		pb.setDeptName("三峡大学科研管理系统");
		pb.setPsnEmail(pbDto.getPsn_email());
		pb.setPsnName(pbDto.getPsn_name());
		pb.setPsnCode(Long.valueOf(personCode));
		pb.setPsnTel(pbDto.getPsn_tel());
		pb.setPsnMobile(pbDto.getPsn_tel2());
		pb.setLaboryLocation(pbDto.getResearch_direction());
		pb.setSubjectArea(pbDto.getSubject_area());
		pb.setAchievementCb(pbDto.getYear_achievement());
		pb.setZhName(pbDto.getZh_name());
		pb.setStatus("0");
		pb.setXml(xml);
		pb.setCreateDate(new Date());
		pb.setUpdateDate(new Date());
		pb.setUpdatePsn(SecurityUtils.getCurrentUserId());
		pb.setCreatePsn(SecurityUtils.getCurrentUserId());
		if (pb_id != null) {
			pbInfoDao.delete(pb_id);
			constFileDictionaryDao.deleteFileDictionary("pb_import", pb_id, fileCode);
			pb.setCreateDate(createDate);
			pb.setCreatePsn(createPsn);
		}
		pbInfoDao.getSession().saveOrUpdate(pb);
		List<PbInfo> tempList = pbInfoDao.getByPbName(pbDto.getZh_name());
		PbInfo temp = tempList.get(0);
		return temp.getPbId();
	}
}