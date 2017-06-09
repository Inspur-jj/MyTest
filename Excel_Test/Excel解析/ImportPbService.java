package com.iris.pb.service;

import java.io.File;
import java.io.Serializable;
import java.util.List;

import org.dom4j.DocumentException;

import com.iris.core.exception.ServiceException;
import com.iris.core.remote.http.Remote;
import com.iris.grid.datatable.DtConditionContainer;
import com.iris.grid.datatable.DtPageContainer;
import com.iris.pb.dto.PbImportDto;

/**
 * 基地平台导入 相关接口
 * 
 * @author HK
 * 
 */
@Remote
public interface ImportPbService extends Serializable {

	public DtPageContainer getImportHistoryList(DtConditionContainer conditionContainer);

	public List<List<? extends PbImportDto>> analyzeImpFile(File importPrpFile) throws ServiceException;

	public Long saveImpPbDto(PbImportDto pbDto, String fileCode) throws DocumentException, NumberFormatException,
			ServiceException;

}
