package com.mall.station.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.mall.api.finance.dto.CommissionDetailDTO;
import com.mall.api.finance.dto.FinanceAccountDTO;
import com.mall.api.finance.dto.FinanceAccountFlowDTO;
import com.mall.api.finance.dto.WithdrawApplyDTO;
import com.mall.api.station.dto.StationMessageDTO;
import com.mall.api.station.vo.StationFinanceQueryVO;
import com.mall.api.station.vo.StationMessageQueryVO;
import com.mall.api.station.vo.StationWithdrawApplyVO;
import com.mall.common.exception.BusinessException;
import com.mall.common.page.PageResult;
import com.mall.common.result.CommonErrorCode;
import com.mall.station.convert.StationConvert;
import com.mall.station.entity.FinAccount;
import com.mall.station.entity.FinAccountFlow;
import com.mall.station.entity.FinCommissionDetail;
import com.mall.station.entity.FinWithdrawApply;
import com.mall.station.entity.MsgRecord;
import com.mall.station.mapper.FinAccountFlowMapper;
import com.mall.station.mapper.FinAccountMapper;
import com.mall.station.mapper.FinCommissionDetailMapper;
import com.mall.station.mapper.FinWithdrawApplyMapper;
import com.mall.station.mapper.MsgRecordMapper;
import com.mall.station.service.StationFinanceService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional(readOnly = true)
public class StationFinanceServiceImpl extends StationReadSupport implements StationFinanceService {

    private static final Long ACCOUNT_NORMAL = 1L;
    private static final Long WITHDRAW_WAIT_AUDIT = 10L;
    private static final Long WITHDRAW_CANCELLED = 40L;
    private static final Long WITHDRAW_PAY_WAIT = 10L;
    private static final Long FLOW_WITHDRAW_FREEZE = 3L;
    private static final Long FLOW_WITHDRAW_CANCEL_UNFREEZE = 7L;
    private static final Long FLOW_DIRECTION_IN = 1L;
    private static final Long FLOW_DIRECTION_OUT = 2L;
    private static final Long MSG_READ = 1L;
    private static final DateTimeFormatter NO_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final FinAccountMapper accountMapper;
    private final FinAccountFlowMapper accountFlowMapper;
    private final FinCommissionDetailMapper commissionDetailMapper;
    private final FinWithdrawApplyMapper withdrawApplyMapper;
    private final MsgRecordMapper msgRecordMapper;
    private final StationConvert stationConvert;

    public StationFinanceServiceImpl(
            final FinAccountMapper accountMapper,
            final FinAccountFlowMapper accountFlowMapper,
            final FinCommissionDetailMapper commissionDetailMapper,
            final FinWithdrawApplyMapper withdrawApplyMapper,
            final MsgRecordMapper msgRecordMapper,
            final StationConvert stationConvert) {
        this.accountMapper = accountMapper;
        this.accountFlowMapper = accountFlowMapper;
        this.commissionDetailMapper = commissionDetailMapper;
        this.withdrawApplyMapper = withdrawApplyMapper;
        this.msgRecordMapper = msgRecordMapper;
        this.stationConvert = stationConvert;
    }

    @Override
    public PageResult<FinanceAccountDTO> pageAccounts(final StationFinanceQueryVO query) {
        final StationFinanceQueryVO safeQuery = query == null ? new StationFinanceQueryVO() : query;
        final LambdaQueryWrapper<FinAccount> wrapper = new LambdaQueryWrapper<FinAccount>()
                .and(StringUtils.hasText(safeQuery.getKeyword()), nested -> nested
                        .like(FinAccount::getAccountNo, safeQuery.getKeyword()))
                .eq(safeQuery.getSubjectType() != null, FinAccount::getSubjectType, safeQuery.getSubjectType())
                .eq(safeQuery.getSubjectId() != null, FinAccount::getSubjectId, safeQuery.getSubjectId())
                .eq(safeQuery.getStatus() != null, FinAccount::getStatus, safeQuery.getStatus())
                .orderByDesc(FinAccount::getId);
        return toPage(safeQuery, accountMapper, wrapper, stationConvert::toFinanceAccountDTO);
    }

    @Override
    public FinanceAccountDTO getAccount(final Long id) {
        return getById(id, accountMapper, stationConvert::toFinanceAccountDTO, "财务账户");
    }

    @Override
    public PageResult<FinanceAccountFlowDTO> pageAccountFlows(final StationFinanceQueryVO query) {
        final StationFinanceQueryVO safeQuery = query == null ? new StationFinanceQueryVO() : query;
        final LambdaQueryWrapper<FinAccountFlow> wrapper = new LambdaQueryWrapper<FinAccountFlow>()
                .and(StringUtils.hasText(safeQuery.getKeyword()), nested -> nested
                        .like(FinAccountFlow::getFlowNo, safeQuery.getKeyword())
                        .or()
                        .like(FinAccountFlow::getBizNo, safeQuery.getKeyword()))
                .eq(safeQuery.getAccountId() != null, FinAccountFlow::getAccountId, safeQuery.getAccountId())
                .eq(safeQuery.getSubjectType() != null, FinAccountFlow::getSubjectType, safeQuery.getSubjectType())
                .eq(safeQuery.getSubjectId() != null, FinAccountFlow::getSubjectId, safeQuery.getSubjectId())
                .eq(safeQuery.getFlowType() != null, FinAccountFlow::getFlowType, safeQuery.getFlowType())
                .eq(safeQuery.getDirection() != null, FinAccountFlow::getDirection, safeQuery.getDirection())
                .orderByDesc(FinAccountFlow::getId);
        return toPage(safeQuery, accountFlowMapper, wrapper, stationConvert::toFinanceAccountFlowDTO);
    }

    @Override
    public FinanceAccountFlowDTO getAccountFlow(final Long id) {
        return getById(id, accountFlowMapper, stationConvert::toFinanceAccountFlowDTO, "账户流水");
    }

    @Override
    public PageResult<CommissionDetailDTO> pageCommissions(final StationFinanceQueryVO query) {
        final StationFinanceQueryVO safeQuery = query == null ? new StationFinanceQueryVO() : query;
        final LambdaQueryWrapper<FinCommissionDetail> wrapper = new LambdaQueryWrapper<FinCommissionDetail>()
                .and(StringUtils.hasText(safeQuery.getKeyword()), nested -> nested
                        .like(FinCommissionDetail::getCommissionNo, safeQuery.getKeyword()))
                .eq(safeQuery.getSubjectType() != null, FinCommissionDetail::getSubjectType,
                        safeQuery.getSubjectType())
                .eq(safeQuery.getSubjectId() != null, FinCommissionDetail::getSubjectId, safeQuery.getSubjectId())
                .eq(safeQuery.getStationId() != null, FinCommissionDetail::getStationId, safeQuery.getStationId())
                .eq(safeQuery.getStatus() != null, FinCommissionDetail::getStatus, safeQuery.getStatus())
                .orderByDesc(FinCommissionDetail::getId);
        return toPage(safeQuery, commissionDetailMapper, wrapper, stationConvert::toCommissionDetailDTO);
    }

    @Override
    public CommissionDetailDTO getCommission(final Long id) {
        return getById(id, commissionDetailMapper, stationConvert::toCommissionDetailDTO, "佣金明细");
    }

    @Override
    public PageResult<WithdrawApplyDTO> pageWithdraws(final StationFinanceQueryVO query) {
        final StationFinanceQueryVO safeQuery = query == null ? new StationFinanceQueryVO() : query;
        final LambdaQueryWrapper<FinWithdrawApply> wrapper = new LambdaQueryWrapper<FinWithdrawApply>()
                .and(StringUtils.hasText(safeQuery.getKeyword()), nested -> nested
                        .like(FinWithdrawApply::getWithdrawNo, safeQuery.getKeyword()))
                .eq(safeQuery.getAccountId() != null, FinWithdrawApply::getAccountId, safeQuery.getAccountId())
                .eq(safeQuery.getSubjectType() != null, FinWithdrawApply::getSubjectType,
                        safeQuery.getSubjectType())
                .eq(safeQuery.getSubjectId() != null, FinWithdrawApply::getSubjectId, safeQuery.getSubjectId())
                .eq(safeQuery.getAuditStatus() != null, FinWithdrawApply::getAuditStatus,
                        safeQuery.getAuditStatus())
                .eq(safeQuery.getPayStatus() != null, FinWithdrawApply::getPayStatus, safeQuery.getPayStatus())
                .orderByDesc(FinWithdrawApply::getId);
        return toPage(safeQuery, withdrawApplyMapper, wrapper, stationConvert::toWithdrawApplyDTO);
    }

    @Override
    public WithdrawApplyDTO getWithdraw(final Long id) {
        return getById(id, withdrawApplyMapper, stationConvert::toWithdrawApplyDTO, "提现申请");
    }

    @Override
    @Transactional
    public WithdrawApplyDTO applyWithdraw(final StationWithdrawApplyVO request) {
        if (request == null) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "提现申请不能为空");
        }
        validateWithdrawRequest(request);
        final FinAccount account = accountMapper.selectById(request.getAccountId());
        if (account == null) {
            throw new BusinessException(CommonErrorCode.NOT_FOUND, "财务账户不存在");
        }
        if (!Objects.equals(account.getStatus(), ACCOUNT_NORMAL)) {
            throw new BusinessException(CommonErrorCode.CONFLICT, "账户已冻结，不能申请提现");
        }
        final BigDecimal availableAmount = zeroIfNull(account.getAvailableAmount());
        if (availableAmount.compareTo(request.getWithdrawAmount()) < 0) {
            throw new BusinessException(CommonErrorCode.CONFLICT, "账户可用余额不足");
        }
        final LocalDateTime now = LocalDateTime.now();
        final String withdrawNo = buildNo("WD", now);
        final FinWithdrawApply apply = new FinWithdrawApply();
        prepareCreate(apply, IdWorker.getId(), now);
        apply.setWithdrawNo(withdrawNo);
        apply.setAccountId(account.getId());
        apply.setSubjectType(account.getSubjectType());
        apply.setSubjectId(account.getSubjectId());
        apply.setWithdrawAmount(request.getWithdrawAmount());
        apply.setReceiveAccountName(request.getReceiveAccountName());
        apply.setReceiveAccountNo(request.getReceiveAccountNo());
        apply.setAuditStatus(WITHDRAW_WAIT_AUDIT);
        apply.setPayStatus(WITHDRAW_PAY_WAIT);
        apply.setAuditAccountId(null);
        apply.setAuditTime(null);
        apply.setAuditRemark(null);
        freezeAccount(account, request.getWithdrawAmount(), withdrawNo, now);
        withdrawApplyMapper.insert(apply);
        return stationConvert.toWithdrawApplyDTO(apply);
    }

    @Override
    @Transactional
    public WithdrawApplyDTO cancelWithdraw(final Long id) {
        if (id == null) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "提现申请ID不能为空");
        }
        final FinWithdrawApply apply = withdrawApplyMapper.selectById(id);
        if (apply == null) {
            throw new BusinessException(CommonErrorCode.NOT_FOUND, "提现申请不存在");
        }
        if (Objects.equals(apply.getAuditStatus(), WITHDRAW_CANCELLED)) {
            return stationConvert.toWithdrawApplyDTO(apply);
        }
        if (!Objects.equals(apply.getAuditStatus(), WITHDRAW_WAIT_AUDIT)) {
            throw new BusinessException(CommonErrorCode.CONFLICT, "只有待审核提现申请才能撤回");
        }
        final FinAccount account = accountMapper.selectById(apply.getAccountId());
        if (account == null) {
            throw new BusinessException(CommonErrorCode.NOT_FOUND, "财务账户不存在");
        }
        final LocalDateTime now = LocalDateTime.now();
        unfreezeAccount(account, apply.getWithdrawAmount(), apply.getWithdrawNo(), now);
        apply.setAuditStatus(WITHDRAW_CANCELLED);
        apply.setAuditTime(now);
        apply.setAuditRemark("申请人撤回");
        prepareUpdate(apply, now);
        withdrawApplyMapper.updateById(apply);
        return stationConvert.toWithdrawApplyDTO(apply);
    }

    @Override
    public PageResult<StationMessageDTO> pageMessages(final StationMessageQueryVO query) {
        final StationMessageQueryVO safeQuery = query == null ? new StationMessageQueryVO() : query;
        final LambdaQueryWrapper<MsgRecord> wrapper = new LambdaQueryWrapper<MsgRecord>()
                .and(StringUtils.hasText(safeQuery.getKeyword()), nested -> nested
                        .like(MsgRecord::getMsgNo, safeQuery.getKeyword())
                        .or()
                        .like(MsgRecord::getTitle, safeQuery.getKeyword())
                        .or()
                        .like(MsgRecord::getContent, safeQuery.getKeyword()))
                .eq(safeQuery.getReceiverType() != null, MsgRecord::getReceiverType, safeQuery.getReceiverType())
                .eq(safeQuery.getReceiverId() != null, MsgRecord::getReceiverId, safeQuery.getReceiverId())
                .eq(safeQuery.getChannelType() != null, MsgRecord::getChannelType, safeQuery.getChannelType())
                .eq(safeQuery.getReadStatus() != null, MsgRecord::getReadStatus, safeQuery.getReadStatus())
                .eq(safeQuery.getSendStatus() != null, MsgRecord::getSendStatus, safeQuery.getSendStatus())
                .eq(StringUtils.hasText(safeQuery.getBizType()), MsgRecord::getBizType, safeQuery.getBizType())
                .eq(StringUtils.hasText(safeQuery.getBizNo()), MsgRecord::getBizNo, safeQuery.getBizNo())
                .orderByDesc(MsgRecord::getId);
        return toPage(safeQuery, msgRecordMapper, wrapper, stationConvert::toStationMessageDTO);
    }

    @Override
    public StationMessageDTO getMessage(final Long id) {
        return getById(id, msgRecordMapper, stationConvert::toStationMessageDTO, "消息");
    }

    @Override
    @Transactional
    public StationMessageDTO markMessageRead(final Long id) {
        if (id == null) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "消息ID不能为空");
        }
        final MsgRecord message = msgRecordMapper.selectById(id);
        if (message == null) {
            throw new BusinessException(CommonErrorCode.NOT_FOUND, "消息不存在");
        }
        if (Objects.equals(message.getReadStatus(), MSG_READ)) {
            return stationConvert.toStationMessageDTO(message);
        }
        final LocalDateTime now = LocalDateTime.now();
        message.setReadStatus(MSG_READ);
        prepareUpdate(message, now);
        msgRecordMapper.updateById(message);
        return stationConvert.toStationMessageDTO(message);
    }

    private void validateWithdrawRequest(final StationWithdrawApplyVO request) {
        if (request.getAccountId() == null) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "账户ID不能为空");
        }
        if (request.getWithdrawAmount() == null || request.getWithdrawAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "提现金额必须大于0");
        }
        if (!StringUtils.hasText(request.getReceiveAccountName())) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "收款账户名不能为空");
        }
        if (!StringUtils.hasText(request.getReceiveAccountNo())) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "收款账号不能为空");
        }
    }

    private void freezeAccount(
            final FinAccount account,
            final BigDecimal amount,
            final String withdrawNo,
            final LocalDateTime now) {
        final BigDecimal beforeAvailable = zeroIfNull(account.getAvailableAmount());
        account.setAvailableAmount(beforeAvailable.subtract(amount));
        account.setFrozenAmount(zeroIfNull(account.getFrozenAmount()).add(amount));
        prepareUpdate(account, now);
        accountMapper.updateById(account);
        insertAccountFlow(account, FLOW_WITHDRAW_FREEZE, FLOW_DIRECTION_OUT, amount, beforeAvailable,
                account.getAvailableAmount(), withdrawNo, "提现申请冻结", now);
    }

    private void unfreezeAccount(
            final FinAccount account,
            final BigDecimal amount,
            final String withdrawNo,
            final LocalDateTime now) {
        final BigDecimal beforeAvailable = zeroIfNull(account.getAvailableAmount());
        final BigDecimal beforeFrozen = zeroIfNull(account.getFrozenAmount());
        if (beforeFrozen.compareTo(amount) < 0) {
            throw new BusinessException(CommonErrorCode.CONFLICT, "账户冻结余额不足，不能撤回提现");
        }
        account.setAvailableAmount(beforeAvailable.add(amount));
        account.setFrozenAmount(beforeFrozen.subtract(amount));
        prepareUpdate(account, now);
        accountMapper.updateById(account);
        insertAccountFlow(account, FLOW_WITHDRAW_CANCEL_UNFREEZE, FLOW_DIRECTION_IN, amount, beforeAvailable,
                account.getAvailableAmount(), withdrawNo, "提现撤回解冻", now);
    }

    private void insertAccountFlow(
            final FinAccount account,
            final Long flowType,
            final Long direction,
            final BigDecimal amount,
            final BigDecimal beforeAmount,
            final BigDecimal afterAmount,
            final String bizNo,
            final String remark,
            final LocalDateTime now) {
        final FinAccountFlow flow = new FinAccountFlow();
        prepareCreate(flow, IdWorker.getId(), now);
        flow.setFlowNo(buildNo("FLOW", now));
        flow.setAccountId(account.getId());
        flow.setSubjectType(account.getSubjectType());
        flow.setSubjectId(account.getSubjectId());
        flow.setFlowType(flowType);
        flow.setDirection(direction);
        flow.setAmount(amount);
        flow.setBeforeAmount(beforeAmount);
        flow.setAfterAmount(afterAmount);
        flow.setBizNo(bizNo);
        flow.setRemark(remark);
        accountFlowMapper.insert(flow);
    }

    private BigDecimal zeroIfNull(final BigDecimal amount) {
        return amount == null ? BigDecimal.ZERO : amount;
    }

    private String buildNo(final String prefix, final LocalDateTime now) {
        return prefix + NO_TIME_FORMATTER.format(now) + IdWorker.getIdStr();
    }
}
