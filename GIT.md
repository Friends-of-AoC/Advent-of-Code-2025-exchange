# Working with Gitlab & GIT

## Onboarding onto MagentaCICD (Gitlab)

### Authentication

The login is only possible with two-factor authentication (2FA). The second factor is a one time password (OTP), which you can generate e.g. with the app FreeOTP or Google Authenticator, available for [Android](https://play.google.com/store/apps/details?id=org.fedorahosted.freeotp) and [iOS](https://apps.apple.com/de/app/freeotp-authenticator/id872559395). 

The setup of your OTP app is initiated on the first login. If you lose the app because of any reason, you need to contact our [support](mailto:devops@telekom.de) and request the reset of the OTP setup for your account.

>   You will only have to associate your account with a 2FA (2nd Factor App) the first time you login to MagentaCICD.

>   You will have to provide OTP after logging in with your email and password *every* time you close the browser/start a new session.

### Joining the PU Hessen Group

After [Authentication](#authentication) please click on [here](https://gitlab.devops.telekom.de/pu-hessen), or search for `PU-Hessen` in Gitlab, and request to join the group.

You will receive an email once you have been added to the group.

>   This is only a one-time action.

### Personal Access Token

When using development tools locally, you will need to authenticate with Gitlab to pull/push code. Instead of using your username and password, you have to create a *Personal Access Token*.

A *Personal Access Token* acts as your password when using local development tools so that you do not have to provide a OTP every time.

To create your personal access token:

1. Navigate to your Gitlab [Profile](https://gitlab.devops.telekom.de/-/profile) page, and then [Access Tokens](https://gitlab.devops.telekom.de/-/profile/personal_access_tokens).

2. On the field `Name`, type a name of your choice (it only serves to identify a particular token, should you want to revoke it at some point).
  
3. On the field `Scopes`, select `api` and leave all other scopes unchecked.

4. Scroll to the end of the page, and click the friendly and green `Create personal access token` button.

5. Your *Personal Access Token* is displayed on the screen. **Keep this code safe** it is like your password - store it somewhere safe, because it will not be displayed again!

>   You can use your *Personal Access Token* as a password in any tools that request it (git client, etc.)

### Personal Access Token Persistence

You can store your credentials on your local development machine by issuing the following git command (you need a git client first):

```
git config --global url."https://{username}:{token}@gitlab.devops.telekom.de"
```

>   Your username is your name as it appears in your email address, without `@telekom.de`. `%26` (i.e. `carlos.lozano-diez`)

> If you are using a GUI-based client, it will take care of persisting your credentials for you.

### Forking a Project

A fork is a copy of an original (origin) repository that you put in another namespace
where you can experiment and apply changes that you can later decide whether or
not to share, without affecting the original project.

1. On the project’s home page that you want to *fork*, in the top right, click the **Fork** button. ![](./resources/gitlab-fork.png)

2. Click a namespace to fork to. Only namespaces you have Developer and higher permissions for are shown. ![](https://docs.gitlab.com/ee/user/project/repository/img/forking_workflow_choose_namespace_v13_2.png)

>   The fork is created. The permissions you have in the namespace are the permissions you will have in the fork.

### Refreshing a Forked Project

Please refer to [Refreshing a Fork](https://forum.gitlab.com/t/refreshing-a-fork/32469) on how to update your forked project with any updated from the original project (from which the fork was made). 

### Cloning a Project

Cloning a project is the process of downloading code from a repository (or a fork of the repository) to your local development environment.

1. On the project’s home page that you want to clone, in the top right, click the blue `Clone` button, and select copy the address under `Clone with HTTPS`. ![](./resources/gitlab-clone.png)

2. Use the copied address with git by issuing the command `git clone https://<copied_address>` in a black folder of your local machine. Alternatively, use the copied address in your GUI-based git client.

### Working with GIT in Visual Studio Code

Please refer to [Using Version Control in VS Code](https://code.visualstudio.com/docs/editor/versioncontrol#_cloning-a-repository) for a good overview of how to work with VScode and git.

This tutorial on [Git version control in VS Code](https://code.visualstudio.com/docs/introvideos/versioncontrol) is also useful, covering more advanced concepts such as branching, diffs and merges.

