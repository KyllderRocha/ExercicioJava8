package br.com.nomaroma.presentation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.omg.Messaging.SyncScopeHelper;

import br.com.nomaroma.entities.Account;
import br.com.nomaroma.entities.AccountEnum;
import br.com.nomaroma.entities.Client;
import br.com.nomaroma.service.BankService;
import br.com.nomaroma.service.ServiceFactory;

/**
 * OBSERVAÇÕES: 
 * NÃO é permitido o uso de nenhuma estrutura de repetição (for, while, do-while).
 * Tente, ao máximo, evitar o uso das estruturas if, else if, else e switch-case
 * 
 * @author VictorLira
 *
 */
public class Main {
	
	private static BankService service = ServiceFactory.getService();
	
	public static void main(String[] args) {
		//1
		//imprimirNomesClientes();
		//2
		//imprimirMediaSaldos();
		//3
		imprimirPaisClienteMaisRico();
		//4
		//imprimirSaldoMedio(1);
		//5
		//imprimirClientesComPoupanca();
		//6
		//getEstadoClientes(1);
		//7
		//getNumerosContas("Brazil");
		//8
		//getMaiorSaldo("client8@bank.com");
		//9
		//sacar(1,1,100);
		//10
		//depositar("Brazil",1000);
		//11
		//transferir(1,1,2,100);
		//12
		//getContasConjuntas(service.listClients().stream().filter(c -> c.getAddress().getCountry().equals("Brazil")).collect(Collectors.toList()));
		//13
		//getSomaContasEstado("Brazil");
		//14
		//getEmailsClientesContasConjuntas();
		//15
		//isPrimo(13);
		//16
		//getFatorial(5);
		
	}
	
	/**
	 * 1. Imprima na tela o nome e e-mail de todos os clientes (sem repetição), usando o seguinte formato:
	 * Victor Lira - vl@cin.ufpe.br
	 */
	public static void imprimirNomesClientes() {
		service
			.listClients()
			.stream()
			.distinct()
			.forEach( c -> System.out.println(c.getName()+" - "+c.getEmail()));
	}
	
	/**
	 * 2. Imprima na tela o nome do cliente e a média do saldo de suas contas, ex:
	 * Victor Lira - 352
	 */
	public static void imprimirMediaSaldos() {
			service
			.listClients()
			.stream()
			.forEach( c -> {
				OptionalDouble d = c.getAccounts()
				.stream()
				.mapToDouble(a -> a.getBalance())
				.average();
				System.out.println(c.getName()+" - "+ (d.isPresent() ? ""+d.getAsDouble() : "Empty"));
				}
			);
	}
	
	/**
	 * 3. Considerando que só existem os países "Brazil" e "United States", 
	 * imprima na tela qual deles possui o cliente mais rico, ou seja,
	 * com o maior saldo somando todas as suas contas.
	 */
	public static void imprimirPaisClienteMaisRico() {
			double max = 
				service
				.listClients()
				.stream()
				.distinct()
				.mapToDouble( c -> 
					c.getAccounts()
					.stream()
					.mapToDouble(a -> a.getBalance())
					.sum()
				).max()
				.getAsDouble();
			
			service
			.listClients()
			.stream()
			.distinct()
			.
			filter(c -> 
				c.getAccounts()
				.stream()
				.mapToDouble(a -> a.getBalance())
				.sum()==max)
			.forEach(c->System.out.println(c.getAddress().getCountry()));
	}
	
	/**
	 * 4. Imprime na tela o saldo médio das contas da agência
	 * @param agency
	 */
	public static void imprimirSaldoMedio(int agency) {
		double aux=service
			.listAccounts()
			.stream()
			.filter(c -> c.getAgency()==agency)
			.mapToDouble(c -> c.getBalance())
			.average()
			.getAsDouble();
		System.out.println(aux);
			
	}
	
	/**
	 * 5. Imprime na tela o nome de todos os clientes que possuem conta poupança (tipo SAVING)
	 */
	public static void imprimirClientesComPoupanca() {
		service
			.listAccounts()
			.stream()
			.filter(c -> c.getType()==AccountEnum.SAVING)
			.map(c -> c.getClient().getName())
			.distinct()
			.forEach(System.out::println);
	}
	
	/**
	 * 6.
	 * @param agency
	 * @return Retorna uma lista de Strings com o "estado" de todos os clientes da agência
	 */
	public static List<String> getEstadoClientes(int agency) {
		return service
				.listAccounts()
				.stream()
				.filter(a -> a.getAgency() == agency)
				.map(a -> a.getClient().getAddress().getState())
				.distinct()
				.collect(Collectors.toList());
	}
	
	/**
	 * 7.
	 * @param country
	 * @return Retorna uma lista de inteiros com os números das contas daquele país
	 */
	public static int[] getNumerosContas(String country) {
		return service
			.listAccounts()
			.stream()
			.filter(a -> a.getClient().getAddress().getCountry() == country)
			.mapToInt(a -> a.getNumber())
			.toArray();
	}
	
	/**
	 * 8.
	 * Retorna o somatório dos saldos das contas do cliente em questão 
	 * @param clientEmail
	 * @return
	 */
	public static double getMaiorSaldo(String clientEmail) {
		return service
			.listClients()
			.stream()
			.filter(c -> c.getEmail() == clientEmail)
			.mapToDouble(c -> c.getAccounts().stream().mapToDouble( a-> a.getBalance()).sum())
			.findFirst()
			.getAsDouble();
	}
	
	/**
	 * 9.
	 * Realiza uma operação de saque na conta de acordo com os parâmetros recebidos
	 * @param agency
	 * @param number
	 * @param value
	 */
	public static void sacar(int agency, int number, double value) {
		service
			.listAccounts()
			.stream()
			.filter(a -> a.getAgency() == agency && a.getNumber() == number)
			.forEach(a-> a.setBalance(a.getBalance()-value));
			
	}
	
	/**
	 * 10. Realiza um deposito para todos os clientes do país em questão	
	 * @param country
	 * @param value
	 */
	public static void depositar(String country, double value) {
		service
			.listAccounts()
			.stream()
			.filter(a -> a.getClient().getAddress().getCountry() == country)
			.forEach(a -> a.setBalance(a.getBalance()+value));
	}
	
	/**
	 * 11. Realiza uma transferência entre duas contas de uma agência.
	 * @param agency - agência das duas contas
	 * @param numberSource - conta a ser debitado o dinheiro
	 * @param numberTarget - conta a ser creditado o dinheiro
	 * @param value - valor da transferência
	 */
	public static void transferir(int agency, int numberSource, int numberTarget, double value) {
		service
			.listAccounts()
			.stream()
			.filter(a -> a.getAgency() == agency && a.getNumber() == numberSource)
			.forEach(a-> a.setBalance(a.getBalance()-value));
		service
			.listAccounts()
			.stream()
			.filter(a -> a.getAgency() == agency && a.getNumber() == numberTarget)
			.forEach(a-> a.setBalance(a.getBalance()+value));
	}
	
	/**
	 * 12.
	 * @param clients
	 * @return Retorna uma lista com todas as contas conjuntas (JOINT) dos clientes
	 */
	public static List<Account> getContasConjuntas(List<Client> clients) {
		return service
			.listAccounts()
			.stream()
			.filter(a -> clients.contains(a.getClient()) && a.getType() == AccountEnum.JOINT)
			.collect(Collectors.toList());
			
	}
	
	/**
	 * 13.
	 * @param state
	 * @return Retorna uma lista com o somatório dos saldos de todas as contas do estado 
	 */
	public static double getSomaContasEstado(String state) {
		 return service
		 	.listAccounts()
		 	.stream()
		 	.filter(a -> a.getClient().getAddress().getState() == state)
		 	.mapToDouble(a -> a.getBalance())
		 	.sum();
	}
	
	/**
	 * 14.
	 * @return Retorna um array com os e-mails de todos os clientes que possuem contas conjuntas
	 */
	public static String[] getEmailsClientesContasConjuntas() {
		return (String[]) service
				.listAccounts()
				.stream()
				.filter(a -> a.getType() == AccountEnum.JOINT)
				.map(a -> a.getClient().getEmail())
				.distinct()
				.toArray();
	}
	
	/**
	 * 15.
	 * @param number
	 * @return Retorna se o número é primo ou não
	 */
	public static boolean isPrimo(int number) {
		return IntStream.range(1, number+1)
		.reduce(0,(a,b) -> number%b== 0 ? ++a:a) == 2;
	}
	
	/**
	 * 16.
	 * @param number
	 * @return Retorna o fatorial do número
	 */
	public static int getFatorial(int number) {
		return IntStream.range(1, number+1)
				.reduce(1,(a,b) -> a*b);
	}
}
