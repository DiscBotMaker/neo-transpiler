<div align="center">
  <h1><strong>NeoTranspiler</strong></h1>
  <div>
    <a href="https://github.com/vyfor/cord.nvim/stargazers"><img src="https://img.shields.io/github/stars/vyfor/cord.nvim?style=for-the-badge" alt="Stargazers"></a>
    <a href="https://github.com/DiscBotMaker/neo-transpiler/blob/master/LICENSE"><img src="https://img.shields.io/github/license/vyfor/cord.nvim?style=for-the-badge" alt="LICENSE"></a>
    <a href="https://github.com/DiscBotMaker/neo-transpiler/forks"><img src="https://img.shields.io/github/forks/vyfor/cord.nvim?style=for-the-badge" alt="Forks"></a>
  </div>
  <h3>🚀 <strong>NeoTranspiler</strong> is a compiler utility for DBMScript, a custom programming language, all written in Java</h3>
<!--   <img src="https://github.com/vyfor/cord.nvim/assets/92883017/d2e46243-2bef-4c73-bb3f-6d10edc2a2f4" alt="Cord Banner"> -->
</div>

## Libraries
[libutil](https://github.com/xNoerPlaysCodes/libutil) - Utils library (net.noerlol.util)          <br>
[libcli](https://github.com/xNoerPlaysCodes/libcli)   - Cli Args library (net.noerlol.cliargs)

# API
## Install [WIP]
```sh
> echo "Working on it!"
```

# Example Usage

### Setting up your environment

```sh
> neotrans -S
```
Here `-S` is shorthand for `--setup` and sets up the files and folders in the current directory

### Compiling

```sh
> neotrans -b
```
Here `-b` is shorthand for `--build` with the output in the `build/` directory

### Running

```sh
> neotrans -r
```

Here `-r` is shorthand for `--run` and runs the compiled sources,<br>
You can even **combine** options:

```sh
> neotrans -b -r
```
will build then run...
<br>
For a more comprehensive help:
```sh
> neotrans -h
```

# Downloading and Running

### Downloading

- Download the latest release from the releases tab

### Running

Prerequisites:
- Java JVM and JRE 17 or higher

now ...

- Run it how you run any java jarfile with the `java -jar neotranspiler.jar --help`


# Optional aliasing to `neotrans`
If you want to alias `java -jar neotranspiler.jar` to `neotrans` like in the examples given above:

### Linux and macOS
- Open your `~/.bashrc` or `~/.zshrc` file and at the end of it add the line `alias neotrans='java -jar /path/to/neotranspiler.jar'`<br>
- Run the command `source ~/.zshrc` or `source ~/.bashrc` depending on which you edited.

replace /path/to/neotranspiler.jar with the actual path.<br>
make sure `java` is in your `$PATH`

### Windows
... ahh windows, truly an *operating system* of all time

*I don't know how to alias something in windows so you're gonna have to figure it out yourself*

# Compiling from source
### Linux and macOS
- Clone the git repo `git clone https://github.com/DiscBotMaker/neo-transpiler.git`

now that you've cloned the repo..

Prerequisites
- `mvn` (Maven) in your `$PATH`

now run the command `mvn clean package` ...

`cd target` to go to the `target` directory and the jar file without `original` in it's name is the compiled output

### Windows

- Clone the git repo `git clone https://github.com/DiscBotMaker/neo-transpiler.git`

now that you've cloned the repo..

Prerequisites
- `mvn` (Maven) in your `%PATH%`

now run the command `mvn clean package` ...

`cd target` to go to the `target` directory and the jar file without `original` in it's name is the compiled output
