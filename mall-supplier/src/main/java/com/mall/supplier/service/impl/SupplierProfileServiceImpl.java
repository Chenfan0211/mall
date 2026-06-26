package com.mall.supplier.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.api.supplier.dto.SupplierContactDTO;
import com.mall.api.supplier.dto.SupplierContractDTO;
import com.mall.api.supplier.dto.SupplierDTO;
import com.mall.api.supplier.dto.SupplierQualificationDTO;
import com.mall.api.supplier.vo.SupplierContactQueryVO;
import com.mall.api.supplier.vo.SupplierContractQueryVO;
import com.mall.api.supplier.vo.SupplierQualificationQueryVO;
import com.mall.api.supplier.vo.SupplierQueryVO;
import com.mall.common.exception.BusinessException;
import com.mall.common.page.PageResult;
import com.mall.common.result.CommonErrorCode;
import com.mall.supplier.convert.SupplierConvert;
import com.mall.supplier.entity.SupContract;
import com.mall.supplier.entity.SupQualification;
import com.mall.supplier.entity.SupSupplier;
import com.mall.supplier.entity.SupSupplierContact;
import com.mall.supplier.mapper.SupContractMapper;
import com.mall.supplier.mapper.SupQualificationMapper;
import com.mall.supplier.mapper.SupSupplierContactMapper;
import com.mall.supplier.mapper.SupSupplierMapper;
import com.mall.supplier.service.SupplierProfileService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional(readOnly = true)
public class SupplierProfileServiceImpl implements SupplierProfileService {

    private final SupSupplierMapper supplierMapper;
    private final SupSupplierContactMapper contactMapper;
    private final SupQualificationMapper qualificationMapper;
    private final SupContractMapper contractMapper;
    private final SupplierConvert supplierConvert;

    public SupplierProfileServiceImpl(
            final SupSupplierMapper supplierMapper,
            final SupSupplierContactMapper contactMapper,
            final SupQualificationMapper qualificationMapper,
            final SupContractMapper contractMapper,
            final SupplierConvert supplierConvert) {
        this.supplierMapper = supplierMapper;
        this.contactMapper = contactMapper;
        this.qualificationMapper = qualificationMapper;
        this.contractMapper = contractMapper;
        this.supplierConvert = supplierConvert;
    }

    @Override
    public PageResult<SupplierDTO> pageSuppliers(final SupplierQueryVO query) {
        final SupplierQueryVO safeQuery = query == null ? new SupplierQueryVO() : query;
        final LambdaQueryWrapper<SupSupplier> wrapper = new LambdaQueryWrapper<SupSupplier>()
                .and(StringUtils.hasText(safeQuery.getKeyword()), nested -> nested
                        .like(SupSupplier::getSupplierNo, safeQuery.getKeyword())
                        .or()
                        .like(SupSupplier::getSupplierName, safeQuery.getKeyword()))
                .eq(safeQuery.getCityId() != null, SupSupplier::getCityId, safeQuery.getCityId())
                .eq(safeQuery.getStatus() != null, SupSupplier::getStatus, safeQuery.getStatus())
                .eq(safeQuery.getAuditStatus() != null, SupSupplier::getAuditStatus, safeQuery.getAuditStatus())
                .orderByDesc(SupSupplier::getId);
        final Page<SupSupplier> page = supplierMapper.selectPage(
                Page.of(safeQuery.getPageNum(), safeQuery.getPageSize()), wrapper);
        return PageResult.of(page.getTotal(), page.getRecords().stream()
                .map(supplierConvert::toSupplierDTO)
                .toList());
    }

    @Override
    public SupplierDTO getSupplier(final Long id) {
        if (id == null) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "供应商ID不能为空");
        }
        final SupSupplier supplier = supplierMapper.selectById(id);
        if (supplier == null) {
            throw new BusinessException(CommonErrorCode.NOT_FOUND, "供应商不存在");
        }
        return supplierConvert.toSupplierDTO(supplier);
    }

    @Override
    public PageResult<SupplierContactDTO> pageContacts(final SupplierContactQueryVO query) {
        final SupplierContactQueryVO safeQuery = query == null ? new SupplierContactQueryVO() : query;
        final LambdaQueryWrapper<SupSupplierContact> wrapper = new LambdaQueryWrapper<SupSupplierContact>()
                .eq(safeQuery.getSupplierId() != null, SupSupplierContact::getSupplierId, safeQuery.getSupplierId())
                .eq(safeQuery.getContactType() != null, SupSupplierContact::getContactType,
                        safeQuery.getContactType())
                .eq(safeQuery.getStatus() != null, SupSupplierContact::getStatus, safeQuery.getStatus())
                .orderByDesc(SupSupplierContact::getDefaultFlag)
                .orderByDesc(SupSupplierContact::getId);
        final Page<SupSupplierContact> page = contactMapper.selectPage(
                Page.of(safeQuery.getPageNum(), safeQuery.getPageSize()), wrapper);
        return PageResult.of(page.getTotal(), page.getRecords().stream()
                .map(supplierConvert::toSupplierContactDTO)
                .toList());
    }

    @Override
    public SupplierContactDTO getContact(final Long id) {
        if (id == null) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "供应商联系人ID不能为空");
        }
        final SupSupplierContact contact = contactMapper.selectById(id);
        if (contact == null) {
            throw new BusinessException(CommonErrorCode.NOT_FOUND, "供应商联系人不存在");
        }
        return supplierConvert.toSupplierContactDTO(contact);
    }

    @Override
    public PageResult<SupplierQualificationDTO> pageQualifications(final SupplierQualificationQueryVO query) {
        final SupplierQualificationQueryVO safeQuery = query == null ? new SupplierQualificationQueryVO() : query;
        final LambdaQueryWrapper<SupQualification> wrapper = new LambdaQueryWrapper<SupQualification>()
                .and(StringUtils.hasText(safeQuery.getKeyword()), nested -> nested
                        .like(SupQualification::getQualificationNo, safeQuery.getKeyword())
                        .or()
                        .like(SupQualification::getCertificateNo, safeQuery.getKeyword()))
                .eq(safeQuery.getSupplierId() != null, SupQualification::getSupplierId, safeQuery.getSupplierId())
                .eq(safeQuery.getQualificationType() != null, SupQualification::getQualificationType,
                        safeQuery.getQualificationType())
                .eq(safeQuery.getAuditStatus() != null, SupQualification::getAuditStatus,
                        safeQuery.getAuditStatus())
                .orderByDesc(SupQualification::getId);
        final Page<SupQualification> page = qualificationMapper.selectPage(
                Page.of(safeQuery.getPageNum(), safeQuery.getPageSize()), wrapper);
        return PageResult.of(page.getTotal(), page.getRecords().stream()
                .map(supplierConvert::toSupplierQualificationDTO)
                .toList());
    }

    @Override
    public SupplierQualificationDTO getQualification(final Long id) {
        if (id == null) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "供应商资质ID不能为空");
        }
        final SupQualification qualification = qualificationMapper.selectById(id);
        if (qualification == null) {
            throw new BusinessException(CommonErrorCode.NOT_FOUND, "供应商资质不存在");
        }
        return supplierConvert.toSupplierQualificationDTO(qualification);
    }

    @Override
    public PageResult<SupplierContractDTO> pageContracts(final SupplierContractQueryVO query) {
        final SupplierContractQueryVO safeQuery = query == null ? new SupplierContractQueryVO() : query;
        final LambdaQueryWrapper<SupContract> wrapper = new LambdaQueryWrapper<SupContract>()
                .and(StringUtils.hasText(safeQuery.getKeyword()), nested -> nested
                        .like(SupContract::getContractNo, safeQuery.getKeyword())
                        .or()
                        .like(SupContract::getContractName, safeQuery.getKeyword()))
                .eq(safeQuery.getSupplierId() != null, SupContract::getSupplierId, safeQuery.getSupplierId())
                .eq(safeQuery.getStatus() != null, SupContract::getStatus, safeQuery.getStatus())
                .orderByDesc(SupContract::getId);
        final Page<SupContract> page = contractMapper.selectPage(
                Page.of(safeQuery.getPageNum(), safeQuery.getPageSize()), wrapper);
        return PageResult.of(page.getTotal(), page.getRecords().stream()
                .map(supplierConvert::toSupplierContractDTO)
                .toList());
    }

    @Override
    public SupplierContractDTO getContract(final Long id) {
        if (id == null) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "供应商合同ID不能为空");
        }
        final SupContract contract = contractMapper.selectById(id);
        if (contract == null) {
            throw new BusinessException(CommonErrorCode.NOT_FOUND, "供应商合同不存在");
        }
        return supplierConvert.toSupplierContractDTO(contract);
    }
}
