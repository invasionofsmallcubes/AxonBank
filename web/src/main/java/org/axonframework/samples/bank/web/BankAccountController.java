/*
 * Copyright (c) 2016. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.axonframework.samples.bank.web;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.samples.bank.api.bankaccount.CreateBankAccountCommand;
import org.axonframework.samples.bank.api.bankaccount.DepositMoneyCommand;
import org.axonframework.samples.bank.api.bankaccount.WithdrawMoneyCommand;
import org.axonframework.samples.bank.query.bankaccount.BankAccountEntry;
import org.axonframework.samples.bank.query.bankaccount.BankAccountRepository;
import org.axonframework.samples.bank.web.dto.BankAccountDto;
import org.axonframework.samples.bank.web.dto.DepositDto;
import org.axonframework.samples.bank.web.dto.WithdrawalDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
@MessageMapping("/bank-accounts")
public class BankAccountController {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private final CommandGateway commandGateway;
    private final BankAccountRepository bankAccountRepository;

    public BankAccountController(CommandGateway commandGateway, BankAccountRepository bankAccountRepository) {
        this.commandGateway = commandGateway;
        this.bankAccountRepository = bankAccountRepository;
    }

    @SubscribeMapping
    public Iterable<BankAccountEntry> all() {

        LOGGER.info("GETALL");

        return bankAccountRepository.findAllByOrderByIdAsc();
    }

    @SubscribeMapping("/{id}")
    public BankAccountEntry get(@DestinationVariable String id) {

        LOGGER.info("GET");


        return bankAccountRepository.findOne(id);
    }

    @MessageMapping("/create")
    public void create(BankAccountDto bankAccountDto) {
        String id = UUID.randomUUID().toString();
        CreateBankAccountCommand command = new CreateBankAccountCommand(id, bankAccountDto.getOverdraftLimit());

        LOGGER.info("CREATE");

        commandGateway.send(command);
    }

    @MessageMapping("/withdraw")
    public void withdraw(WithdrawalDto depositDto) {
        WithdrawMoneyCommand command = new WithdrawMoneyCommand(depositDto.getBankAccountId(), depositDto.getAmount());

        LOGGER.info("WITHDRAW");


        commandGateway.send(command);
    }

    @MessageMapping("/deposit")
    public void deposit(DepositDto depositDto) {
        DepositMoneyCommand command = new DepositMoneyCommand(depositDto.getBankAccountId(), depositDto.getAmount());

        LOGGER.info("DEPOSIT");

        commandGateway.send(command);
    }

}
