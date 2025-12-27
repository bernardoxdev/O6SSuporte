# 🛠️ O6SSuporte

![Java](https://img.shields.io/badge/Java-17+-red?style=flat-square&logo=java)
![Maven](https://img.shields.io/badge/Maven-Build-blue?style=flat-square&logo=apache-maven)
![License](https://img.shields.io/badge/License-MIT-green?style=flat-square)
![GitHub stars](https://img.shields.io/github/stars/bernardoxdev/O6SSuporte?style=flat-square)
![GitHub forks](https://img.shields.io/github/forks/bernardoxdev/O6SSuporte?style=flat-square)
![GitHub issues](https://img.shields.io/github/issues/bernardoxdev/O6SSuporte?style=flat-square)

**Sistema de suporte para servidores de Minecraft**, desenvolvido para organizar, gerenciar e facilitar o atendimento técnico dentro da infraestrutura do servidor.

---

## 📌 Sobre o projeto

O **O6SSuporte** é um plugin de minecraft escrito em **Java Spigot**, focado em fornecer um sistema de suporte/helpdesk para servidores.  
A ideia central é permitir que jogadores ou membros da staff criem e acompanhem chamados, enquanto a equipe administra e responde de forma organizada.

---

## 🚀 Funcionalidades

✔ Sistema de tickets de suporte  
✔ Organização de fluxo de atendimento

---

## Comandos

- `/suporte <duvida>` - Abre um ticket de suporte
- `/tickets` - Lista os tickets abertos
- `/suportestaff`:
    - `/suportestaff adicionar <jogador> <cargo>` - Comando para adicionar um novo player a staff
    - `/suportestaff remover <jogador>` - Comando para remover um player da staff
    - `/suportestaff listar` - Comando para listar os players da staff
    - `/suportestaff update <jogador> <cargo>` - Comando para atualizar o cargo de um player da staff

---

## 📁 Estrutura do Projeto

```
O6SSuporte/
├── src/
│   └── main/
│       └── java/
│           └── org/bernardo/O6SSuporte/
│               ├── APIs/
│               │   ├── HeadsAPI.java
│               │   └── SubCommand.java
│               ├── database/
│               │   ├── DatabaseAPI.java
│               │   ├── DatabaseConnection.java
│               │   └── DatabaseSetup.java
│               ├── staff/
│               │   ├── subcommands/
│               │   └── CommandStaff.java
│               │   │   ├── AdicionarCMD.java
│               │   │   ├── ListarCMD.java
│               │   │   ├── RemoverCMD.java
│               │   │   └── UpdateCMD.java
│               ├── tickets/
│               │   ├── APIs/
│               │   │   ├── FiltrosTypes.java
│               │   │   └── TicketsAPI.java
│               │   ├── subcommands/
│               │   │   ├── FecharCMD.java
│               │   │   ├── ResponderCMD.java
│               │   │   └── VerCMD.java
│               │   ├── templates/
│               │   │   └── TicketsTemplate.java
│               │   ├── CommandSuporte.java
│               │   └── CommandTickets.java
│               └── O6SSuporte.java
├── target/
├── pom.xml
└── README.md
```

---

## 💻 Requisitos

- ActionbarAPI

---

## 📄 Licença

Licença **GPL-3.0 License**.

---

## 📬 Contato

Bernardo Castro  
GitHub: https://github.com/bernardoxdev
Discord: bernardocastro___